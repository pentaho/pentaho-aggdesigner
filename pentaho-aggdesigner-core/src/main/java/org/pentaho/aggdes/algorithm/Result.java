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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.algorithm;

import java.util.List;
import java.io.PrintWriter;

import org.pentaho.aggdes.model.Aggregate;

/**
 * Recommendations produced by an aggregate table advisor job.
 */
public interface Result {
    /**
     * Returns a list of aggregate tables.
     *
     * @return list of aggregate tables
     */
    List<Aggregate> getAggregates();

    /**
     * Returns a list of cost/benefit for each aggregate.
     *
     * @return list of cost/benefit metrics
     */
    List<Algorithm.CostBenefit> getCostBenefits();

    /**
     * Prints a textual description of the result.
     *
     * @param pw Print writer
     */
    void describe(PrintWriter pw);
}

// End Result.java
