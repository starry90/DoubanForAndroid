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

package com.starry.sqlitedao.query;

import android.database.Cursor;

import com.starry.sqlitedao.AbstractDao;

public class CountQuery<T> extends AbstractQuery<T> {

    static <T2> CountQuery<T2> create(AbstractDao<T2> dao, String sql, Object[] initialValues) {
        return new CountQuery<T2>(dao, sql, toStringArray(initialValues));
    }


    private CountQuery(AbstractDao<T> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
    }

    /**
     * Returns the count (number of results matching the query). Uses SELECT COUNT (*) sematics.
     */
    public long count() {
        long count = 0;
        Cursor cursor = daoAccess.query(new String[]{"count(*) AS count"}, sqlWhere, parameters, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
        return count;
    }

}
