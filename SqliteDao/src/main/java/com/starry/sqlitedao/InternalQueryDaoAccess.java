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

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

/**
 * For internal use by greenDAO only.
 */
public final class InternalQueryDaoAccess<T> {
    private final AbstractDao<T> dao;

    public InternalQueryDaoAccess(AbstractDao<T> abstractDao) {
        dao = abstractDao;
    }

    public List<T> loadAllAndCloseCursor(Cursor cursor) {
        return dao.loadAllAndCloseCursor(cursor);
    }

    public T loadUniqueAndCloseCursor(Cursor cursor) {
        return dao.loadUniqueAndCloseCursor(cursor);
    }

    public int delete(String where, String[] selectionArgs) {
        return dao.delete(where, selectionArgs);
    }

    public int update(ContentValues values, String where, String[] selectionArgs) {
        return dao.update(values, where, selectionArgs);
    }

    public Cursor query(String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return dao.query(projection, selection, selectionArgs, sortOrder);
    }


}