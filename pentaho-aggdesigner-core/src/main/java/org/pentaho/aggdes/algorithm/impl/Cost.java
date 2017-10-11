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

package org.pentaho.aggdes.algorithm.impl;

/**
 * Represents the cost of materializing an
 * {@link org.pentaho.aggdes.model.Aggregate}.
 *
 * @author jhyde
 * @version $Id: Cost.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class Cost {
    public double cost;
    public int benefitCount;
    public double benefit;

    public void copyFrom(Cost other) {
        this.cost = other.cost;
        this.benefitCount = other.benefitCount;
        this.benefit = other.benefit;
    }

    public String toString() {
        return "{cost=" + cost +
            ", benefit=" + benefit +
            ", count=" + benefitCount + "}";
    }
}

// End Cost.java
