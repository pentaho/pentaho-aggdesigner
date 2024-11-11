/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
