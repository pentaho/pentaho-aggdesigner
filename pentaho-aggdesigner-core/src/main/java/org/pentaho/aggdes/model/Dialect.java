/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.aggdes.model;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Description of the capabilities of a database, its SQL dialect, and driver.
 *
 * @author jhyde
 * @version $Id: $
 * @since Apr 26, 2008
 */
public interface Dialect {
    
    final String NL = System.getProperty("line.separator");
    
    /**
     * Appends to a buffer one or more identifiers, quoted appropriately for
     * this Dialect.
     *
     * <p>For example, in the SQL Server dialect,
     * {@code quoteIdentifier(buf, "foo", "bar baz")} generates
     * {@code "[foo].[bar baz]"}.</p>
     *
     * @param buf Buffer
     * @param names One of more identifers
     */
    void quoteIdentifier(StringBuilder buf, String... names);

    /**
     * Returns the SQL type string for integer values.
     *
     * <p>This value is assumed to the be type returned by the {@code COUNT()}
     * aggregate function.
     * Typically {@code "INTEGER"}.
     *
     * @return integer type string
     */
    String getIntegerTypeString();

    /**
     * Returns the SQL type string for integer values.
     *
     * <p>This value is assumed to the be type returned by the {@code SUM()}
     * and {@code AVG()} aggregate functions.
     * Typically {@code "DOUBLE"}.
     *
     * @return double type string
     */
    String getDoubleTypeString();

    String removeInvalidIdentifierCharacters(String str);
    
    /**
     * Returns the maximum length of a table name.
     *
     * @return maximum length of a table name
     */
    int getMaximumTableNameLength();
    
    /**
     * Returns the maximum length of a column name.
     *
     * @return maximum length of a column name
     */
    int getMaximumColumnNameLength();
    
    // DDL Generation Section of the Dialect
    
    /**
     * Appends a single-line comment to a string builder.
     *
     * @param s String
     * @param buf String builder
     */
    void comment(
        StringBuilder buf,
        String s);

    /**
     * Terminates a SQL command.
     *
     * <p>For most dialects, this appends {@code ";{newline}"} to {@code buf}.
     *
     * @param buf String builder
     */
    void terminateCommand(StringBuilder buf);
    
    /**
     * Returns true if database column ddl supports precision , ie INT(10) vs. INT
     * 
     * @param meta database metadata object
     * @param type the type of the column
     * @return true if supports precision
     * @throws SQLException
     */
    public boolean supportsPrecision(DatabaseMetaData meta, String type) throws SQLException;
}

// End Dialect.java
