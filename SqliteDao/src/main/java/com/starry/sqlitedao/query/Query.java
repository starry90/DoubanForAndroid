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
import com.starry.sqlitedao.DaoException;

import java.util.List;

/**
 * A repeatable query returning entities.
 *
 * @param <T> The entity class the query will return results for.
 * @author Markus
 */
public class Query<T> extends AbstractQuery<T> {

    static <T2> Query<T2> create(AbstractDao<T2> dao, String sql, Object[] initialValues) {
        return new Query<T2>(dao, sql, toStringArray(initialValues.clone()));
    }

    private Query(AbstractDao<T> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
    }

    /**
     * Executes the query and returns the result as a list containing all entities loaded into memory.
     */
    public List<T> list() {
        Cursor query = daoAccess.query(null, sqlWhere, parameters, null);
        return daoAccess.loadAllAndCloseCursor(query);
    }


    /**
     * Executes the query and returns the unique result or null.
     *
     * @return Entity or null if no matching entity was found
     * @throws DaoException if the result is not unique
     */
    public T unique() {
        Cursor query = daoAccess.query(null, sqlWhere, parameters, null);
        return daoAccess.loadUniqueAndCloseCursor(query);
    }

    /**
     * Executes the query and returns the unique result (never null).
     *
     * @return Entity
     * @throws DaoException if the result is not unique or no entity was found
     */
    public T uniqueOrThrow() {
        T entity = unique();
        if (entity == null) {
            throw new DaoException("No entity found for query");
        }
        return entity;
    }

}
