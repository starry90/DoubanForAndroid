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
import com.starry.sqlitedao.InternalQueryDaoAccess;

/**
 * A repeatable query returning entities.
 *
 * @param <T> The entity class the query will return results for.
 * @author Markus
 */
// TODO support long, double and other types, not just Strings, for parameters
// TODO Make parameters setable by Property (if unique in parameters)
// TODO Make query compilable
abstract class AbstractQuery<T> {
    protected final String sqlWhere;
    protected final String[] parameters;
    protected final InternalQueryDaoAccess<T> daoAccess;

    protected static String[] toStringArray(Object[] values) {
        int length = values.length;
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = values[i];
            if (object != null) {
                strings[i] = object.toString();
            } else {
                strings[i] = null;
            }
        }
        return strings;
    }

    protected AbstractQuery(AbstractDao<T> dao, String sqlWhere, String[] parameters) {
        this.sqlWhere = sqlWhere;
        this.parameters = parameters;
        this.daoAccess = new InternalQueryDaoAccess<T>(dao);
    }

    protected void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }


}
