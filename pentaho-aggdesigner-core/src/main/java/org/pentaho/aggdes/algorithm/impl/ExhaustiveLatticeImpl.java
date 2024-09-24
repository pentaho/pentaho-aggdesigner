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

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.util.BitSetPlus;
import org.pentaho.aggdes.algorithm.Algorithm;

/**
 * Implementation of the {@link org.pentaho.aggdes.algorithm.impl.Lattice}
 * data structure that instantiates every node
 * ({@link org.pentaho.aggdes.model.Aggregate}) in the lattice.
 *
 * <p>This implementation has the advantage of being straightforward, but it
 * uses a lot of memory and is only feasible for small schemas.
 *
 * @see ExhaustiveLatticeAlgorithm
 *
 * @author jhyde
 * @version $Id: ExhaustiveLatticeImpl.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
class ExhaustiveLatticeImpl extends LatticeImpl {
    ExhaustiveLatticeImpl(Schema schema) {
        super(schema);
    }

    public AggregateImpl chooseAggregate(
        double maxCost,
        double minCostBenefitRatio,
        Cost cost) {

        AggregateImpl bestAggregate = null;
        // for all aggregates
        long max = 1 << schema.getAttributes().size();
        double bestBenefit = 0;
        int bestBenefitCount = 0;
        int[] benefitCount0 = {0};
        for (long i = 0; i < max; ++i) {
            BitSetPlus bits = toBits(i);
            AggregateImpl aggregate = getAggregate(bits);
            if (aggregate.materialized) {
                continue;
            }
            double benefit = getBenefit(aggregate, benefitCount0);
            if (benefit > bestBenefit) {
                bestBenefit = benefit;
                bestAggregate = aggregate;
                bestBenefitCount = benefitCount0[0];
            }
        }
        cost.benefit = bestBenefit;
        cost.benefitCount = bestBenefitCount;
        return bestAggregate;
    }

    /**
     * Materializes an aggregate, and propagates the cost improvements to
     * children.
     */
    public void materialize(AggregateImpl aggregate) {
        super.materialize(aggregate);
        double aggregateCost =
                aggregate.cost = aggregate.estimateRowCount();
        for (AggregateImpl child : nonMaterializedDescendants(aggregate, true)) {
            if (aggregateCost < child.cost) {
                child.cost = aggregateCost;
            }
        }
    }

    public Algorithm.CostBenefit costBenefitOf(final AggregateImpl aggregate) {
        double aggregateCount = Math.pow(2d, schema.getAttributes().size());
        double aggregateCost =
                aggregate.cost = aggregate.estimateRowCount();
        double costSaving = 0d;
        for (AggregateImpl child : nonMaterializedDescendants(aggregate, true)) {
            if (aggregateCost < child.cost) {
                costSaving = (child.cost - aggregateCost);
            }
        }
        final double costSavingPerQuery = costSaving / aggregateCount;

        return new AlgorithmImpl.CostBenefitImpl(
            schema, aggregate, costSavingPerQuery);
    }
}

// End ExhaustiveLatticeImpl.java
