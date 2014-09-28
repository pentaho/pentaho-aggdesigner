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
 * A field in a star schema.
 *
 * <p>Each Attribute knows its join path to the fact table of the star schema.
 * An attribute may be the key or name of a level, or a property of a member
 * such as phone number. An attribute may reside in the fact table, but more
 * commonly resides in a dimension table.</p>
 *
 * <p>A {@link Measure} is a special kind of Attribute.
 *
 * @see Schema
 * @see Table
 */
public interface Attribute {
    /**
     * Returns a description of this attribute for tracing purposes.
     *
     * @return label of this attribute
     */
    String getLabel();

    /**
     * Returns the table that this attribute belongs to.
     *
     * @return table this attribute belongs to
     */
    Table getTable();

    /**
     * Estimates the average number of bytes required to store a value of
     * this attribute.
     *
     * @return estimated bytes per value
     */
    double estimateSpace();

    /**
     * Returns a suggestion for the name of a column in which to store this
     * attribute as part of an aggregate table.
     *
     * <p>The suggestion does not need to be unique within the table or less
     * than the database's column name limit, but the implementation should
     * try to generate a name that is likely to be unique and descriptive in
     * the first 20 or so characters.
     *
     * @return candidate column name
     */
    String getCandidateColumnName();

    /**
     * Returns a description of the SQL data type of this attribute in the
     * given dialect. For example, "VARCHAR(20) NOT NULL".
     *
     * @param dialect Dialect
     * @return SQL data type
     */
    String getDatatype(Dialect dialect);


    /**
     * Returns a list of Ancestor Attributes that make this attribute unique
     *
     * @return ancestor attributes
     */
    List<Attribute> getAncestorAttributes();
}

// End Attribute.java
