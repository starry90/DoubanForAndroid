package com.starry.douban.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容提供者数据库访问基类
 *
 * @author Starry Jerry
 * @since 2019-12-27
 */
public abstract class BaseDao<T> {

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
     * 解析Cursor成对象
     *
     * @param cursor A Cursor object
     * @return T
     */
    protected abstract T parseCursor(Cursor cursor);

    /**
     * 获取ContentValues
     *
     * @param t T
     * @return ContentValues
     */
    protected abstract ContentValues getContentValues(T t);

    /**
     * 获取ContentResolver
     *
     * @return ContentResolver
     */
    private ContentResolver getResolver() {
        return getContext().getContentResolver();
    }

    private void closeQuietly(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private StringBuilder appendPlaceholderAnd(String[] keyList) {
        StringBuilder selection = new StringBuilder();
        int size = keyList.length;
        for (int i = 0; i < size; i++) {
            selection.append(keyList[i])
                    .append("=?");
            if (i < size - 1) {
                selection.append(" and ");
            }
        }
        return selection;
    }

    private <E> String appendIn(String key, E[] values) {
        StringBuilder selection = new StringBuilder(key);
        selection.append(" in (");
        for (E value : values) {
            selection.append(value).append(",");
        }
        return selection.substring(0, selection.length() - 1) + ")";
    }

    private <C> Object getFromCursor(Cursor cursor, String column, Class<C> cls) {
        Object item = null;
        if (cls == String.class) {
            item = cursor.getString(cursor.getColumnIndex(column));
        } else if (cls == Integer.class) {
            item = cursor.getInt(cursor.getColumnIndex(column));
        } else if (cls == Long.class) {
            item = cursor.getLong(cursor.getColumnIndex(column));
        } else if (cls == Float.class) {
            item = cursor.getFloat(cursor.getColumnIndex(column));
        } else if (cls == Double.class) {
            item = cursor.getDouble(cursor.getColumnIndex(column));
        } else if (cls == Short.class) {
            item = cursor.getShort(cursor.getColumnIndex(column));
        }
        return item;
    }

    /**
     * 查询记录条数
     *
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @return 记录条数
     */
    public synchronized int getCount(String selection, String[] selectionArgs) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getResolver().query(getUri(), new String[]{"count(*) AS count"}, selection,
                    selectionArgs, null);
            if (cursor != null) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return count;
    }

    /**
     * 查询记录条数
     *
     * @param selection A filter declaring which rows to return, formatted as an
     *                  SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                  return all rows for the given URI.
     * @return 记录条数
     */
    public int getCount(String selection) {
        return getCount(selection, null);
    }

    /**
     * 按=条件查询记录条数
     *
     * @param key   =条件键
     * @param value =条件值
     * @return 记录条数
     */
    public int getCountByEqual(String key, String value) {
        return getCount(key + "=?", new String[]{value});
    }

    /**
     * 查询出所有记录
     *
     * @return 出所有记录
     */
    public List<T> queryAll() {
        return query(null, null);
    }

    /**
     * 查询出单条记录
     *
     * @param selection A filter declaring which rows to return, formatted as an
     *                  SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                  return all rows for the given URI.
     * @return 单条记录，返回第一次出现的记录
     */
    public T querySingle(String selection) {
        return querySingle(null, selection, null, null);
    }

    /**
     * 查询出单条记录
     *
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @return 单条记录，返回第一次出现的记录
     */
    public T querySingle(String selection, String[] selectionArgs) {
        return querySingle(null, selection, selectionArgs, null);
    }

    /**
     * 查询出单条记录
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
     * @return 单条记录，返回第一次出现的记录
     */
    public synchronized T querySingle(String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            cursor = getResolver().query(getUri(), projection, selection, selectionArgs, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                return parseCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return null;
    }

    /**
     * 查询出单条记录
     *
     * @param column        列名
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param cls           列名类型
     * @return 单条记录，返回第一次出现的记录
     */
    public synchronized <C> C querySingle(String column, String selection,
                                          String[] selectionArgs, String sortOrder, Class<C> cls) {
        Cursor cursor = null;
        try {
            cursor = getResolver().query(getUri(), new String[]{column}, selection, selectionArgs, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                Object item = getFromCursor(cursor, column, cls);
                return (C) item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return null;
    }


    /**
     * 根据条件查询记录
     *
     * @param selection A filter declaring which rows to return, formatted as an
     *                  SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                  return all rows for the given URI.
     * @return 查询到的结果列表
     */
    public List<T> query(String selection) {
        return query(null, selection, null, null);
    }

    /**
     * 根据条件查询记录
     *
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @return 查询到的结果列表
     */
    public List<T> query(String selection, String[] selectionArgs) {
        return query(null, selection, selectionArgs, null);
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
    public synchronized List<T> query(String[] projection, String selection,
                                      String[] selectionArgs, String sortOrder) {
        List<T> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getResolver().query(getUri(), projection, selection, selectionArgs, sortOrder);
            while (cursor != null && cursor.moveToNext()) {
                result.add(parseCursor(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return result;
    }

    /**
     * 根据条件查询记录
     *
     * @param column        列名
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in the order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY
     *                      clause (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @param cls           列名类型
     * @return 查询到的结果列表
     */
    public synchronized <C> List<C> query(String column, String selection,
                                          String[] selectionArgs, String sortOrder, Class<C> cls) {
        List<C> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getResolver().query(getUri(), new String[]{column}, selection, selectionArgs, sortOrder);
            while (cursor != null && cursor.moveToNext()) {
                Object item = getFromCursor(cursor, column, cls);
                result.add((C) item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
        return result;
    }

    /**
     * 根据=条件获取单列记录
     *
     * @param key    =条件键
     * @param value  =条件值
     * @param column 列名
     * @param c      列名类型
     * @param <C>    列名类型
     * @return 单列记录
     */
    public <C> C queryByEqual(String key, String value, String column, Class<C> c) {
        return querySingle(column, key + "=?", new String[]{value}, null, c);
    }

    /**
     * 根据多个=条件获取记录
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @return 单条记录
     */
    public <C> C queryByEqualAnd(String[] keyList, String[] valueList, String column, Class<C> c) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return querySingle(column, selection.toString(), valueList, null, c);
    }

    /**
     * 根据=条件获取单条记录
     *
     * @param key   =条件键
     * @param value =条件值
     * @return 单条记录
     */
    public T queryByEqual(String key, String value) {
        return querySingle(key + "=?", new String[]{value});
    }

    /**
     * 根据多个=条件获取记录
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @return 单条记录
     */
    public T queryByEqualAnd(String[] keyList, String[] valueList) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return querySingle(selection.toString(), valueList);
    }

    /**
     * 根据=条件获取单列记录列表
     *
     * @param key    =条件键
     * @param value  =条件值
     * @param column 列名
     * @param c      列名类型
     * @param <C>    列名类型
     * @return 单列记录列表
     */
    public <C> List<C> queryListByEqual(String key, String value, String column, Class<C> c) {
        return query(column, key + "=?", new String[]{value}, null, c);
    }

    /**
     * 根据=条件获取记录列表
     *
     * @param key   =条件键
     * @param value =条件值
     * @return 记录列表
     */
    public List<T> queryListByEqual(String key, String value) {
        return query(key + "=?", new String[]{value});
    }

    /**
     * 根据多个=条件获取记录列表
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @return 记录列表
     */
    public List<T> queryListByEqualAnd(String[] keyList, String[] valueList) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return query(selection.toString(), valueList);
    }

    /**
     * 根据多个=条件获取单列记录列表
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @param column    列名
     * @param c         列名类型
     * @param <C>       列名类型
     * @return 单列记录列表
     */
    public <C> List<C> queryListByEqualAnd(String[] keyList, String[] valueList, String column, Class<C> c) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return query(column, selection.toString(), valueList, null, c);
    }

    /**
     * 根据in条件获取记录列表
     *
     * @param key    in条件键
     * @param values in条件值列表
     * @return 记录列表
     */
    public <E> List<T> queryListByIn(String key, E[] values) {
        return query(appendIn(key, values), null);
    }

    /**
     * 根据in条件获取记录列表
     *
     * @param key    in条件键
     * @param values in条件值列表
     * @param column 列名
     * @param c      列名类型
     * @param <C>    列名类型
     * @return 记录列表
     */
    public <C, E> List<C> queryListByIn(String key, E[] values, String column, Class<C> c) {
        return query(column, appendIn(key, values), null, null, c);
    }

    /**
     * 插入记录
     *
     * @param values 要插入的对象
     * @return Uri
     */
    public synchronized Uri insert(ContentValues values) {
        Uri uri = null;
        try {
            uri = getResolver().insert(getUri(), values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * 插入记录
     *
     * @param t 要插入的对象
     * @return Uri
     */
    public Uri insert(T t) {
        return insert(getContentValues(t));
    }

    /**
     * 删除所有记录
     *
     * @return 删除的个数
     */
    public int deleteAll() {
        return delete(null, null);
    }

    /**
     * 根据条件删除记录
     *
     * @param where A filter to apply to rows before deleting, formatted as an SQL WHERE clause
     *              (excluding the WHERE itself).
     * @return The number of rows deleted
     */
    public synchronized int delete(String where, String[] selectionArgs) {
        int rowsDeleted = 0;
        try {
            rowsDeleted = getResolver().delete(getUri(), where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsDeleted;
    }

    /**
     * 根据=条件删除记录
     *
     * @param key   =条件键
     * @param value =条件值
     * @return 删除记录数量
     */
    public synchronized int deleteByEqual(String key, String value) {
        return delete(key + "=?", new String[]{value});
    }


    /**
     * 根据多个=条件删除记录
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @return 删除记录数量
     */
    public int deleteByEqualAnd(String[] keyList, String[] valueList) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return delete(selection.toString(), valueList);
    }

    /**
     * 根据in条件获取记录列表
     *
     * @param key    in条件键
     * @param values in条件值列表
     * @return 记录列表
     */
    public <E> int deleteByIn(String key, E[] values) {
        return delete(appendIn(key, values), null);
    }


    /**
     * 更新对象的所有字段
     *
     * @param t     要更新的对象
     * @param where A filter to apply to rows before updating, formatted as an SQL WHERE clause
     *              (excluding the WHERE itself).
     * @return the number of rows updated.
     */
    public synchronized int updateAll(T t, String where, String[] selectionArgs) {
        int rowsUpdated = 0;
        try {
            rowsUpdated = getResolver().update(getUri(), getContentValues(t), where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsUpdated;
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
    public synchronized int update(ContentValues values, String where, String[] selectionArgs) {
        int rowsUpdated = 0;
        try {
            rowsUpdated = getResolver().update(getUri(), values, where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsUpdated;
    }

    /**
     * 根据=条件更新记录
     *
     * @param key   =条件键
     * @param value =条件值
     * @return 更新记录个数
     */
    public int updateByEqual(ContentValues values, String key, String value) {
        return update(values, key + "=?", new String[]{value});
    }

    /**
     * 根据多个=条件更新记录
     *
     * @param keyList   =条件键列表
     * @param valueList =条件值列表
     * @return 更新记录个数
     */
    public int updateByEqualAnd(ContentValues values, String[] keyList, String[] valueList) {
        StringBuilder selection = appendPlaceholderAnd(keyList);
        return update(values, selection.toString(), valueList);
    }

}
