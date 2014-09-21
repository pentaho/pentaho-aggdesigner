/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.model;

import java.util.List;


/**
 * Star schema.
 *
 * <p>A star schema is a collection of tables: a central fact table, and
 * several dimension tables. Each table except the fact table is joined
 * to a parent table.
 */
public interface Schema {
    /**
     * Returns a list of all {@link Table} objects in the schema.
     *
     * @return list of tables
     */
    List<? extends Table> getTables();

    /**
     * Returns a list of {@link Measure} objects in the schema.
     *
     * @return list of measures
     */
    List<Measure> getMeasures();

    /**
     * Returns a list of {@link Dimension} objects in the schema;
     * @return
     */
    List<? extends Dimension> getDimensions();

    /**
     * Returns a list of all attributes in this schema.
     *
     * @return list of all attributes in all tables
     */
    List<Attribute> getAttributes();

    /**
     * Returns the statistics provider for this Schema.
     *
     * @return statistics provider
     */
    StatisticsProvider getStatisticsProvider();

    /**
     * Returns the dialect of the underlying database.
     *
     * @return dialect
     */
    Dialect getDialect();

    /**
     * Converts an aggregate to a SELECT statement.
     * Uses the schema's SQL dialect.
     *
     * @param aggregate Aggregate
     * @param columnNameList List of column names, one per attribute
     * @return SQL SELECT statement to populate aggregate
     */
    String generateAggregateSql(
        Aggregate aggregate,
        List<String> columnNameList);
}

// End Schema.java
