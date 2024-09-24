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


/**
 * A collection of Attributes in a star schema.
 *
 * @see Schema
 * @see Attribute
 *
 * @author jhyde
 * @version $Id$
 * @since Mar 13, 2008
 */
public interface Table {
    /**
     * Returns a description of this table for tracing purposes.
     *
     * @return label of this table
     */
    String getLabel();

    /**
     * Returns this table's parent in the star schema.
     *
     * <p>The fact table's parent is null;
     * a dimension table's parent is either the fact table,
     * or the table one step along the arm of the snowflake towards
     * the fact table.
     *
     * @return parent table
     */
    Table getParent();
}

// End Table.java
