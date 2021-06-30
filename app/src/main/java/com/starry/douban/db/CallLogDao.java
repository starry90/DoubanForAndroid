package com.starry.douban.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.starry.douban.env.AppWrapper;
import com.starry.sqlitedao.AbstractDao;
import com.starry.sqlitedao.DaoConfig;
import com.starry.sqlitedao.Property;


/**
 * 通话记录数据库操作类
 *
 * @author Starry Jerry
 * @since 19-11-20.
 */
public class CallLogDao extends AbstractDao<CallLogDao.CallLogModel> {

    /**
     * Properties of entity Order.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ID = new Property(Long.class, CallLog.Calls._ID);
        public final static Property NUMBER = new Property(String.class, CallLog.Calls.NUMBER);
        public final static Property CACHED_NAME = new Property(String.class, CallLog.Calls.CACHED_NAME);
        public final static Property DURATION = new Property(Long.class, CallLog.Calls.DURATION);
    }

    public CallLogDao(DaoConfig config) {
        super(config);
    }

    @Override
    protected Context getContext() {
        return AppWrapper.getContext();
    }

    @Override
    protected Uri getUri() {
        return CallLog.Calls.CONTENT_URI;
    }

    @Override
    protected CallLogModel readEntity(Cursor cursor) {
        CallLogModel callLogModel = new CallLogModel();
        callLogModel.read(cursor);
        return callLogModel;
    }

    @Override
    protected ContentValues readContentValues(CallLogModel callLogModel) {
        return callLogModel.contentValues();
    }

    public static final class CallLogModel implements BaseDBModel {
        public long _id;
        public String number;
        public String cachedName;
        public long duration;

        @Override
        public void read(Cursor cursor) {
            _id = cursor.getLong(cursor.getColumnIndex(CallLog.Calls._ID));
            number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
        }

        @Override
        public ContentValues contentValues() {
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, number);
            values.put(CallLog.Calls.CACHED_NAME, cachedName);
            values.put(CallLog.Calls.DURATION, duration);
            return values;
        }
    }

}
