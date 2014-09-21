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
