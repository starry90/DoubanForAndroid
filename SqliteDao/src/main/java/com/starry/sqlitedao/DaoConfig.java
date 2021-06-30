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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Internal class used by greenDAO. DaoConfig stores essential data for DAOs, and is hold by AbstractDaoMaster. This
 * class will retrieve the required information from the DAO classes.
 */
public final class DaoConfig implements Cloneable {

    public final Property[] properties;

    public final String[] allColumns;

    public String tablename;

    public DaoConfig(Property[] properties) {
        this.properties = properties;

        allColumns = new String[properties.length];

        for (int i = 0; i < properties.length; i++) {
            Property property = properties[i];
            String name = property.columnName;
            allColumns[i] = name;
        }
    }

    public DaoConfig(Class<? extends AbstractDao<?>> daoClass) {
        try {
            Property[] properties = reflectProperties(daoClass);
            this.properties = properties;

            allColumns = new String[properties.length];

            for (int i = 0; i < properties.length; i++) {
                Property property = properties[i];
                String name = property.columnName;
                allColumns[i] = name;
            }
        } catch (Exception e) {
            throw new DaoException("Could not init DAOConfig", e);
        }
    }

    private static Property[] reflectProperties(Class<? extends AbstractDao<?>> daoClass)
            throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        Class<?> propertiesClass = Class.forName(daoClass.getName() + "$Properties");
        Field[] fields = propertiesClass.getDeclaredFields();

        ArrayList<Property> propertyList = new ArrayList<Property>();
        final int modifierMask = Modifier.STATIC | Modifier.PUBLIC;
        for (Field field : fields) {
            // There might be other fields introduced by some tools, just ignore them (see issue #28)
            if ((field.getModifiers() & modifierMask) == modifierMask) {
                Object fieldValue = field.get(null);
                if (fieldValue instanceof Property) {
                    propertyList.add((Property) fieldValue);
                }
            }
        }

        Property[] properties = new Property[propertyList.size()];
        propertyList.toArray(properties);
        return properties;
    }

    /**
     * Does not copy identity scope.
     */
    public DaoConfig(DaoConfig source) {
        properties = source.properties;
        allColumns = source.allColumns;
    }

    /**
     * Does not copy identity scope.
     */
    @Override
    public DaoConfig clone() {
        return new DaoConfig(this);
    }

}
