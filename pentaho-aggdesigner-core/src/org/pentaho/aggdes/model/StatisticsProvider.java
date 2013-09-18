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

import java.util.List;


/**
 * Source for statistics about a Schema.
 *
 * Statistics include the number of rows in the fact table,
 * the number of distinct values of an attribute,
 * and the number of distinct values of a set of attributes.
 *
 * @author jhyde
 * @version $Id: $
 * @since Mar 16, 2008
 */
public interface StatisticsProvider {
    /**
     * Returns the number of rows in the fact table.
     *
     * @return number of rows in the fact table
     */
    double getFactRowCount();

    /**
     * Returns an estimate of the number of distinct values of a collection
     * of attributes.
     *
     * <p>For example, we would expect <code>getRowCount({gender})</code>
     * to return 2, and <code>getRowCount({gender, maritalStatus})</code>
     * to return 4 (2 * 2).
     *
     * <p>A good implementation of this method should recognize when
     * attributes are dependent: for example, if there are 100 suppliers in
     * 10 cities, <code>getRowCount({city, supplierName})</code> should
     * return 100. Similarly, the amount should be bounded by the number
     * of rows in the table which is the source of the attributes.
     *
     * <p>The algorithm may call this method many times, so a good
     * implementation will compute statistics once only and thereafter
     * return cached results.
     *
     * @see #getFactRowCount()
     *
     * @param attributes List of attributes whose joint distribution is
     *   estimated
     * @return Estimated number of values in the attributes' joint
     *   distribution
     */
    double getRowCount(List<Attribute> attributes);

    /**
     * Returns an estimate of the amount of space (in bytes) required to
     * store a row in an Aggregate composed of the given Attributes.
     *
     * @param attributes List of attributes whose joint distribution is
     *   estimated
     * @return Estimated space in bytes to store a row of attributes
     */
    double getSpace(List<Attribute> attributes);

    double getLoadTime(List<Attribute> attributes);
}

// End StatisticsProvider.java
