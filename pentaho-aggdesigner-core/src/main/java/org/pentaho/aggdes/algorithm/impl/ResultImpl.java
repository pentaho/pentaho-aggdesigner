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
