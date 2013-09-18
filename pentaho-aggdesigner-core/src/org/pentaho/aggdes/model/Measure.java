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
