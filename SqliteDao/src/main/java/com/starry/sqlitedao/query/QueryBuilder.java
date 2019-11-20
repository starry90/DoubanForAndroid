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
import com.starry.sqlitedao.DaoLog;
import com.starry.sqlitedao.Property;
import com.starry.sqlitedao.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds custom entity queries using constraints and parameters and without SQL (QueryBuilder creates SQL for you). To
 * acquire an QueryBuilder, use {@link AbstractDao#queryBuilder()} .
 * Entity properties are referenced by Fields in the "Properties" inner class of the generated DAOs. This approach
 * allows compile time checks and prevents typo errors occuring at buildQuery time.<br/>
 * <br/>
 * Example: Query for all users with the first name "Joe" ordered by their last name. (The class Properties is an inner
 * class of UserDao and should be imported before.)<br/>
 * <code>
 * List<User> joes = dao.queryBuilder().where(Properties.FirstName.eq("Joe")).orderAsc(Properties.LastName).list();
 * </code>
 *
 * @param <T> Entity class to create an query for.
 * @author Markus
 */
public class QueryBuilder<T> {

    /**
     * Set to true to debug the SQL.
     */
    public static boolean LOG_SQL = true;

    /**
     * Set to see the given values.
     */
    public static boolean LOG_VALUES = true;

    private final WhereCollector<T> whereCollector;

    private StringBuilder orderBuilder;

    private final List<Object> values;

    private Integer limit;
    private Integer offset;

    private final AbstractDao<T> dao;


    /**
     * stored with a leading space
     */
    private String stringOrderCollation;

    /**
     * For internal use by greenDAO only.
     */
    public static <T2> QueryBuilder<T2> internalCreate(AbstractDao<T2> dao) {
        return new QueryBuilder<T2>(dao);
    }

    protected QueryBuilder(AbstractDao<T> dao) {
        this.dao = dao;
        values = new ArrayList<Object>();
        whereCollector = new WhereCollector<T>(dao.getProperties());
        stringOrderCollation = " COLLATE NOCASE";
    }

    private void checkOrderBuilder() {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder();
        } else if (orderBuilder.length() > 0) {
            orderBuilder.append(",");
        }
    }

    /**
     * Adds the given conditions to the where clause using an logical AND. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder<T> where(WhereCondition cond, WhereCondition... condMore) {
        whereCollector.add(cond, condMore);
        return this;
    }

    /**
     * Adds the given conditions to the where clause using an logical OR. To create new conditions, use the properties
     * given in the generated dao classes.
     */
    public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        whereCollector.add(or(cond1, cond2, condMore));
        return this;
    }

    /**
     * Creates a WhereCondition by combining the given conditions using OR. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition or(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" OR ", cond1, cond2, condMore);
    }

    /**
     * Creates a WhereCondition by combining the given conditions using AND. The returned WhereCondition must be used
     * inside {@link #where(WhereCondition, WhereCondition...)} or
     * {@link #whereOr(WhereCondition, WhereCondition, WhereCondition...)}.
     */
    public WhereCondition and(WhereCondition cond1, WhereCondition cond2, WhereCondition... condMore) {
        return whereCollector.combineWhereConditions(" AND ", cond1, cond2, condMore);
    }

    /**
     * Adds the given properties to the ORDER BY section using ascending order.
     */
    public QueryBuilder<T> orderAsc(Property... properties) {
        orderAscOrDesc(" ASC", properties);
        return this;
    }

    /**
     * Adds the given properties to the ORDER BY section using descending order.
     */
    public QueryBuilder<T> orderDesc(Property... properties) {
        orderAscOrDesc(" DESC", properties);
        return this;
    }

    private void orderAscOrDesc(String ascOrDescWithLeadingSpace, Property... properties) {
        for (Property property : properties) {
            checkOrderBuilder();
            append(orderBuilder, property);
            if (String.class.equals(property.type) && stringOrderCollation != null) {
                orderBuilder.append(stringOrderCollation);
            }
            orderBuilder.append(ascOrDescWithLeadingSpace);
        }
    }

    /**
     * Adds the given properties to the ORDER BY section using the given custom order.
     */
    public QueryBuilder<T> orderCustom(Property property, String customOrderForProperty) {
        checkOrderBuilder();
        append(orderBuilder, property).append(' ');
        orderBuilder.append(customOrderForProperty);
        return this;
    }

    /**
     * Adds the given raw SQL string to the ORDER BY section. Do not use this for standard properties: orderAsc and
     * orderDesc are preferred.
     */
    public QueryBuilder<T> orderRaw(String rawOrder) {
        checkOrderBuilder();
        orderBuilder.append(rawOrder);
        return this;
    }

    protected StringBuilder append(StringBuilder builder, Property property) {
        whereCollector.checkProperty(property);
        SqlUtils.appendProperty(builder, property);
        return builder;
    }


    /**
     * Limits the number of results returned by queries.
     */
    public QueryBuilder<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Builds a reusable query object (Query objects can be executed more efficiently than creating a QueryBuilder for
     * each execution.
     */
    public Query<T> buildQuery() {
        StringBuilder builder = createSelectBuilder();
        checkAddLimit(builder);
        checkAddOffset(builder);

        String sql = builder.toString();
        checkLog(sql);

        return Query.create(dao, sql, values.toArray());
    }

    private StringBuilder createSelectBuilder() {
        StringBuilder builder = new StringBuilder();
        appendWheres(builder);

        if (orderBuilder != null && orderBuilder.length() > 0) {
            builder.append(" ORDER BY ").append(orderBuilder);
        }
        return builder;
    }

    private int checkAddLimit(StringBuilder builder) {
        int limitPosition = -1;
        if (limit != null) {
            builder.append(" LIMIT ?");
            values.add(limit);
            limitPosition = values.size() - 1;
        }
        return limitPosition;
    }

    private int checkAddOffset(StringBuilder builder) {
        int offsetPosition = -1;
        if (offset != null) {
            if (limit == null) {
                throw new IllegalStateException("Offset cannot be set without limit");
            }
            builder.append(" OFFSET ?");
            values.add(offset);
            offsetPosition = values.size() - 1;
        }
        return offsetPosition;
    }

    /**
     * Builds a reusable query object for deletion (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public DeleteQuery<T> buildDelete() {
        StringBuilder builder = new StringBuilder();
        appendWheres(builder);

        String sqlWhere = builder.toString();
        checkLog(sqlWhere);

        return DeleteQuery.create(dao, sqlWhere, values.toArray());
    }

    public UpdateQuery<T> buildUpdate() {
        StringBuilder builder = new StringBuilder();
        appendWheres(builder);

        String sqlWhere = builder.toString();
        checkLog(sqlWhere);

        return UpdateQuery.create(dao, sqlWhere, values.toArray());
    }

    /**
     * Builds a reusable query object for counting rows (Query objects can be executed more efficiently than creating a
     * QueryBuilder for each execution.
     */
    public CountQuery<T> buildCount() {
        StringBuilder builder = new StringBuilder();
        appendWheres(builder);

        String sqlWhere = builder.toString();
        checkLog(sqlWhere);

        return CountQuery.create(dao, sqlWhere, values.toArray());
    }

    private void appendWheres(StringBuilder builder) {
        values.clear();
        if (!whereCollector.isEmpty()) {
            whereCollector.appendWhereClause(builder, values);
        }
    }

    private void checkLog(String sql) {
        if (LOG_SQL) {
            DaoLog.e("Built SQL for query: " + sql);
        }
        if (LOG_VALUES) {
            DaoLog.e("Values for query: " + values);
        }
    }

}
