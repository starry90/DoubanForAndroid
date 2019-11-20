package com.starry.douban.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.starry.douban.env.AppWrapper;
import com.starry.douban.util.JsonUtil;
import com.starry.log.Logger;
import com.starry.sqlitedao.AbstractDao;
import com.starry.sqlitedao.DaoConfig;
import com.starry.sqlitedao.Property;


/**
 * 通话记录数据库操作类
 *
 * @author Starry Jerry
 * @since 19-11-20.
 */
public class SIMContactsDao extends AbstractDao<SIMContactsDao.Model> {


    /**
     * 一般单卡为： <br>
     * "content://icc/adn" <br>
     * 双卡为 <br>
     * "content://icc/adn/subid/0" <br>
     * "content://icc/adn/subid/1" <br>
     */
    public static Uri uri = Uri.parse("content://icc/adn/subId/0");

    /**
     * Properties of entity Order.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ID = new Property(Long.class, BaseColumns._ID);
        public final static Property TAG = new Property(String.class, "tag");
        public final static Property NAME = new Property(String.class, "name");
        public final static Property NUMBER = new Property(String.class, "number");
        public final static Property EMAILS = new Property(String.class, "emails");
        public final static Property EFID = new Property(String.class, "efid");
        public final static Property INDEX = new Property(String.class, "index");
        public final static Property ANRS = new Property(String.class, "anrs");
    }

    public SIMContactsDao(DaoConfig config) {
        super(config);
    }

    @Override
    protected Context getContext() {
        return AppWrapper.getContext();
    }

    @Override
    protected Uri getUri() {
        return uri;
    }

    @Override
    protected Model readEntity(Cursor cursor) {
        Model model = new Model();
        Logger.e("DaoLog", JsonUtil.toJson(cursor.getColumnNames()));
        model.read(cursor);
        return model;
    }

    @Override
    protected ContentValues readContentValues(Model callLogModel) {
        return callLogModel.contentValues();
    }

    public static final class Model implements BaseDBModel {
        public long _id;
        public String name;
        public String number;
        public String emails;
        public String efid;
        public String index;
        public String anrs;

        @Override
        public void read(Cursor cursor) {
            _id = cursor.getLong(cursor.getColumnIndex(Properties.ID.columnName));
            name = cursor.getString(cursor.getColumnIndex(Properties.NAME.columnName));
            number = cursor.getString(cursor.getColumnIndex(Properties.NUMBER.columnName));
            emails = cursor.getString(cursor.getColumnIndex(Properties.EMAILS.columnName));
            efid = cursor.getString(cursor.getColumnIndex(Properties.EFID.columnName));
            index = cursor.getString(cursor.getColumnIndex(Properties.INDEX.columnName));
            anrs = cursor.getString(cursor.getColumnIndex(Properties.ANRS.columnName));
        }

        @Override
        public ContentValues contentValues() {
            ContentValues values = new ContentValues();
            //sim卡联系人 插入的是 ‘tag’ 查出来的是 ‘name’
            values.put(Properties.TAG.columnName, name);
            values.put(Properties.NUMBER.columnName, number);
            values.put(Properties.EMAILS.columnName, emails);
            return values;
        }
    }

}
