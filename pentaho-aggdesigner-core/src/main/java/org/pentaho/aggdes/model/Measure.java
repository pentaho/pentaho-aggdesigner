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
 * A quantity of interest in a star schema.
 *
 * @author jhyde
 * @version $Id: Measure.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Mar 13, 2008
 */
public interface Measure extends Attribute {
    /**
     * Returns whether this measure is a distinct aggregation, such
     * as <code>distinct-count</code>.
     *
     * <p>Distinct aggregations cannot be rolled up, in general, so more
     * aggregate tables are required for the same performance gain.
     *
     * @return whether the measure is distinct
     */
    boolean isDistinct();
}

// End Measure.java
