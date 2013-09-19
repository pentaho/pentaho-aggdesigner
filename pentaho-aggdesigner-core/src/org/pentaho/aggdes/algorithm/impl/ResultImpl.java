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

package org.pentaho.aggdes.algorithm.impl;

import org.pentaho.aggdes.algorithm.*;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;

/**
 * Implementation of {@link org.pentaho.aggdes.algorithm.Result}.
 *
 * @author jhyde
 * @version $Id: ResultImpl.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class ResultImpl implements Result {
    final List<Aggregate> aggregates = new ArrayList<Aggregate>();

    /**
     * Estimate of the benefit of this set of aggregate tables.
     * (The time saved for a typical set of queries.)
     */
    final double benefit;

    /**
     * An estimate of the total cost of the aggregate tables.
     */
    final double cost;

    /**
     * Cost limit imposed when running the algorithm.
     */
    private final double costLimit;

    private final List<Algorithm.CostBenefit> costBenefitList =
        new ArrayList<Algorithm.CostBenefit>();

    public ResultImpl(
        List<Aggregate> materializedAggregates,
        List<Algorithm.CostBenefit> costBenefitList,
        double costLimit,
        double cost,
        double benefit)
    {
        this.costLimit = costLimit;
        aggregates.addAll(materializedAggregates);
        this.costBenefitList.addAll(costBenefitList);
        this.cost = cost;
        this.benefit = benefit;
    }

    public List<Aggregate> getAggregates() {
        return aggregates;
    }

    public List<Algorithm.CostBenefit> getCostBenefits() {
        return costBenefitList;
    }

    public void describe(PrintWriter pw) {
        int j = -1;
        for (Aggregate aggregate : getAggregates()) {
            ++j;
            pw.print("AggregateTable: ");
            int i = 0;
            for (Attribute attribute : aggregate.getAttributes()) {
                if (i++ > 0) {
                    pw.print(", ");
                }
                pw.print(attribute.getLabel());
            }
            pw.println("; ");
            costBenefitList.get(j).describe(pw);
            pw.println();
        }
        pw.println("Cost limit: " + costLimit);
        pw.println("Actual cost: " + cost);
        pw.println("Benefit: " + benefit);
        pw.println("Cost/benefit ratio: " + cost / benefit);
    }
}

// End ResultImpl.java
