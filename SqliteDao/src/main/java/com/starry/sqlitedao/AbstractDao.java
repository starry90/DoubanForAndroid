/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.starry.sqlitedao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.starry.sqlitedao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all DAOs: Implements entity operations like insert, load, delete, and query.
 * <p>
 * This class is thread-safe.
 *
 * @param <T> Entity type
 * @author Markus
 */
/*
 * When operating on TX, statements, or identity scope the following locking order must be met to avoid deadlocks:
 *
 * 1.) If not inside a TX already, begin a TX to acquire a DB connection (connection is to be handled like a lock)
 *
 * 2.) The DatabaseStatement
 *
 * 3.) identityScope
 */
public abstract class AbstractDao<T> {
    public static final String TAG = "AbstractDao";

    protected final DaoConfig config;

    public AbstractDao(DaoConfig config) {
        this.config = config;
    }

    public String getTablename() {
        return config.tablename;
    }

    public Property[] getProperties() {
        return config.properties;
    }

    public String[] getAllColumns() {
        return config.allColumns;
    }

    private void closeQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * Loads all available entities from the database.
     */
    public List<T> loadAll() {
        Cursor cursor = query(null, null, null, null);
        return loadAllAndCloseCursor(cursor);
    }

    List<T> loadAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllFromCursor(cursor);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return new ArrayList<>();
    }

    /**
     * Reads all available rows from the given cursor and returns a list of entities.
     */
    List<T> loadAllFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        if (count == 0) {
            return new ArrayList<T>();
        }
        List<T> list = new ArrayList<T>(count);
        while (cursor.moveToNext()) {
            list.add(loadCurrent(cursor));
        }

        return list;
    }

    /**
     * Internal use only. Considers identity scope.
     */
    final protected T loadCurrent(Cursor cursor) {
        T entity = readEntity(cursor);
        return entity;
    }

    protected T loadUniqueAndCloseCursor(Cursor cursor) {
        try {
            return loadUnique(cursor);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return null;
    }

    protected T loadUnique(Cursor cursor) {
        boolean available = cursor.moveToFirst();
        if (!available) {
            return null;
        } else if (!cursor.isLast()) {
            throw new DaoException("Expected unique result, but count was " + cursor.getCount());
        }
        return loadCurrent(cursor);
    }

    /**
     * Inserts the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void insertInTx(T... entities) {
        if (entities == null || entities.length == 0) {
            return;
        }
        for (T entity : entities) {
            executeInsert(entity);
        }
    }

    /**
     * Insert an entity into the table associated with a concrete DAO.
     *
     * @return row ID of newly inserted entity
     */
    public long insert(T entity) {
        return SqlUtils.parseUri(executeInsert(entity));
    }

    /**
     * Insert an entity into the table associated with a concrete DAO.
     *
     * @return Uri of newly inserted entity
     */
    public Uri insertReturnUri(T entity) {
        return executeInsert(entity);
    }


    private Uri executeInsert(T entity) {
        return insert(readContentValues(entity));
    }

    public int deleteAll() {
        return delete(null, null);
    }

    public QueryBuilder<T> queryBuilder() {
        return QueryBuilder.internalCreate(this);
    }

    private ContentResolver getResolver() {
        return getContext().getContentResolver();
    }

    /**
     * 插入记录
     *
     * @param values 要插入的对象
     * @return Uri
     */
    protected synchronized Uri insert(ContentValues values) {
        Uri uri = null;
        try {
            uri = getResolver().insert(getUri(), values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * 根据条件删除记录
     *
     * @param where A filter to apply to rows before deleting, formatted as an SQL WHERE clause
     *              (excluding the WHERE itself).
     * @return The number of rows deleted
     */
    protected synchronized int delete(String where, String[] selectionArgs) {
        int rowsDeleted = -1;
        try {
            rowsDeleted = getResolver().delete(getUri(), where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsDeleted;
    }


    /**
     * 更新对象指定的字段
     *
     * @param values The new field values. The key is the column name for the field.
     *               A null value will remove an existing field value.
     * @param where  A filter to apply to rows before updating, formatted as an SQL WHERE clause
     *               (excluding the WHERE itself).
     * @return the number of rows updated
     */
    protected synchronized int update(ContentValues values, String where, String[] selectionArgs) {
        int rowsUpdated = -1;
        try {
            rowsUpdated = getResolver().update(getUri(), values, where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsUpdated;
    }

    /**
     * 根据条件查询记录
     *
     * @param projection    A list of which columns to return. Passing null will
     *                      return all columns, which is inefficient.
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return 查询到的结果列表
     */
    protected synchronized Cursor query(String[] projection, String selection,
                                        String[] selectionArgs, String sortOrder) {
        try {
            return getResolver().query(getUri(), projection, selection, selectionArgs, sortOrder);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    protected abstract Context getContext();

    /**
     * The URI, using the content:// scheme, for the content to
     * retrieve.
     *
     * @return Uri
     */
    protected abstract Uri getUri();

    /**
     * Reads the values from the current position of the given cursor and returns a new entity.
     */
    protected abstract T readEntity(Cursor cursor);

    /**
     * Reads the values from the current entity and returns a new ContentValues.
     *
     * @param t entity
     * @return ContentValues
     */
    protected abstract ContentValues readContentValues(T t);

}
