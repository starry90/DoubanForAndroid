package com.starry.douban;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.starry.douban.db.CallLogDao;
import com.starry.douban.db.SIMContactsDao;
import com.starry.log.Logger;
import com.starry.sqlitedao.DaoConfig;
import com.starry.sqlitedao.query.QueryBuilder;

import org.junit.Test;

import java.util.List;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleDaoTest extends AndroidTestCase {

    private static final String TAG = "ExampleDaoTest";

    @Test
    public void testQuery() {
        DaoConfig daoConfig = new DaoConfig(CallLogDao.class);
        CallLogDao callLogDao = new CallLogDao(daoConfig);
        QueryBuilder<CallLogDao.CallLogModel> queryBuilder = callLogDao.queryBuilder();
        queryBuilder.where(CallLogDao.Properties.CACHED_NAME.isNotNull());
        List<CallLogDao.CallLogModel> list = queryBuilder.buildQuery().list();
        Logger.e(TAG, "DaoLog: " + new Gson().toJson(list));
    }

    @Test
    public void testCount() {
        DaoConfig daoConfig = new DaoConfig(CallLogDao.class);
        CallLogDao callLogDao = new CallLogDao(daoConfig);
        QueryBuilder<CallLogDao.CallLogModel> queryBuilder = callLogDao.queryBuilder();
        long count = queryBuilder.where(CallLogDao.Properties.DURATION.greater(0))
                .buildCount()
                .count();
        Logger.e(TAG, "DaoLog: " + count);
    }

    @Test
    public void testQuerySIMContacts() {
        DaoConfig daoConfig = new DaoConfig(SIMContactsDao.class);
        SIMContactsDao dao = new SIMContactsDao(daoConfig);
        QueryBuilder<SIMContactsDao.Model> queryBuilder = dao.queryBuilder();
        queryBuilder.where(SIMContactsDao.Properties.NUMBER.isNotNull());
        List<SIMContactsDao.Model> list = queryBuilder.buildQuery().list();
        Logger.e(TAG, "DaoLog: " + new Gson().toJson(list));
    }

    @Test
    public void testInsertSIMContacts() {
        DaoConfig daoConfig = new DaoConfig(SIMContactsDao.class);
        SIMContactsDao dao = new SIMContactsDao(daoConfig);
        SIMContactsDao.Model model = new SIMContactsDao.Model();
        model.number = "1008611";
        model.name = "李云龙";
        long id = dao.insert(model);
        Logger.e(TAG, "DaoLog: id " + id);
    }

    @Test
    public void testUpdateSIMContacts() {
        ContentValues values = new ContentValues();
        values.put("tag", "李云龙");
        values.put("number", "1008611");
        values.put("newTag", "李云龙2");
        values.put("newNumber", "1001011");
        int update = getContext().getContentResolver().update(SIMContactsDao.uri, values, null, null);
        Logger.e(TAG, "DaoLog: count " + update);
    }

    @Test
    public void testDeleteSIMContacts() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String tag = "李云龙";
        String number = "1008611";
        String where = "tag='" + tag + "'";
        where += " AND number='" + number + "'";
        //TODO 不能使用  tag = ? AND number = ? ，源码按 AND切割条件，不能匹配点位符
        int delete = contentResolver.delete(SIMContactsDao.uri, where, null);
        Logger.e(TAG, "DaoLog: count " + delete);
    }

}