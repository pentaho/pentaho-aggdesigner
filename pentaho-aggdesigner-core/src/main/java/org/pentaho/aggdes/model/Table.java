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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

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
