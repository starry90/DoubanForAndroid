package com.starry.sqlitedao.query;

import android.content.ContentValues;

import com.starry.sqlitedao.AbstractDao;

public class UpdateQuery<T> extends AbstractQuery<T> {

    static <T2> UpdateQuery<T2> create(AbstractDao<T2> dao, String sqlWhere, Object[] initialValues) {
        return new UpdateQuery<T2>(dao, sqlWhere, toStringArray(initialValues.clone()));
    }

    protected UpdateQuery(AbstractDao<T> dao, String sqlWhere, String[] parameters) {
        super(dao, sqlWhere, parameters);
    }

    public int update(ContentValues contentValues) {
        return daoAccess.update(contentValues, sqlWhere, parameters);
    }
}
