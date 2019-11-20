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

import com.starry.sqlitedao.AbstractDao;

/**
 * A repeatable query for deleting entities.<br/>
 * New API note: this is more likely to change.
 *
 * @param <T> The entity class the query will delete from.
 * @author Markus
 */
public class DeleteQuery<T> extends AbstractQuery<T> {

    static <T2> DeleteQuery<T2> create(AbstractDao<T2> dao, String sql, Object[] initialValues) {
        return new DeleteQuery<T2>(dao, sql, toStringArray(initialValues.clone()));
    }

    private DeleteQuery(AbstractDao<T> dao, String sql, String[] initialValues) {
        super(dao, sql, initialValues);
    }


    /**
     * Deletes all matching entities without detaching them from the identity scope (aka session/cache). Note that this
     * method may lead to stale entity objects in the session cache. Stale entities may be returned when loaded by
     * their
     * primary key, but not using queries.
     */
    public int delete() {
        return daoAccess.delete(sqlWhere, parameters);
    }

}
