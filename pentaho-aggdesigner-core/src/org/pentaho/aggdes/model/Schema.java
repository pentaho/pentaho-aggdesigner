/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License, version 2 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
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
