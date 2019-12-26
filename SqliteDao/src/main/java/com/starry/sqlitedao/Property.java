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

import com.starry.sqlitedao.query.WhereCondition;
import com.starry.sqlitedao.query.WhereCondition.PropertyCondition;

import java.util.Collection;

/**
 * Meta data describing a property mapped to a database column; used to create WhereCondition object used by the query builder.
 *
 * @author Markus
 */
public class Property {
    public final Class<?> type;
    public final String columnName;

    public Property(Class<?> type, String columnName) {
        this.type = type;
        this.columnName = columnName;
    }

    /**
     * Creates an "equal ('=')" condition  for this property.
     */
    public WhereCondition equal(Object value) {
        return new PropertyCondition(this, "=?", value);
    }

    /**
     * Creates an "not equal ('<>')" condition  for this property.
     */
    public WhereCondition notEqual(Object value) {
        return new PropertyCondition(this, "<>?", value);
    }

    /**
     * Creates an "LIKE" condition  for this property.
     */
    public WhereCondition like(String value) {
        return new PropertyCondition(this, " LIKE ?", value);
    }

    /**
     * Creates an "BETWEEN ... AND ..." condition  for this property.
     */
    public WhereCondition between(Object value1, Object value2) {
        Object[] values = {value1, value2};
        return new PropertyCondition(this, " BETWEEN ? AND ?", values);
    }

    /**
     * Creates an "IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition in(Object... inValues) {
        StringBuilder condition = new StringBuilder(" IN (");
        SqlUtils.appendPlaceholders(condition, inValues.length).append(')');
        return new PropertyCondition(this, condition.toString(), inValues);
    }

    /**
     * Creates an "IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition in(Collection<?> inValues) {
        return in(inValues.toArray());
    }

    /**
     * Creates an "NOT IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition notIn(Object... notInValues) {
        StringBuilder condition = new StringBuilder(" NOT IN (");
        SqlUtils.appendPlaceholders(condition, notInValues.length).append(')');
        return new PropertyCondition(this, condition.toString(), notInValues);
    }

    /**
     * Creates an "NOT IN (..., ..., ...)" condition  for this property.
     */
    public WhereCondition notIn(Collection<?> notInValues) {
        return notIn(notInValues.toArray());
    }

    /**
     * Creates an "greater than ('>')" condition  for this property.
     */
    public WhereCondition greater(Object value) {
        return new PropertyCondition(this, ">?", value);
    }

    /**
     * Creates an "less than ('<')" condition  for this property.
     */
    public WhereCondition less(Object value) {
        return new PropertyCondition(this, "<?", value);
    }

    /**
     * Creates an "greater or equal ('>=')" condition  for this property.
     */
    public WhereCondition greaterOrEqual(Object value) {
        return new PropertyCondition(this, ">=?", value);
    }

    /**
     * Creates an "less or equal ('<=')" condition  for this property.
     */
    public WhereCondition lessOrEqual(Object value) {
        return new PropertyCondition(this, "<=?", value);
    }

    /**
     * Creates an "IS NULL" condition  for this property.
     */
    public WhereCondition isNull() {
        return new PropertyCondition(this, " IS NULL");
    }

    /**
     * Creates an "IS NOT NULL" condition  for this property.
     */
    public WhereCondition isNotNull() {
        return new PropertyCondition(this, " IS NOT NULL");
    }

}
