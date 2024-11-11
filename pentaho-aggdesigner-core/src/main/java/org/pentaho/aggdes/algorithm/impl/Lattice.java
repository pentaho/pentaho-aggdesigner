/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.algorithm.impl;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.algorithm.Algorithm;

import java.util.List;

/**
     * Data structure which contains the set of all possible aggregates,
 * arranged so that you can navigate from any aggregate to its parents
 * (aggregates having precisely one more attribute) and to its children
 * (aggregates having precisely one fewer attribute).
 *
 * <p>The top of the lattice is the aggregate with no attributes and one
 * row, that is, the grand total. The bottom of the lattice is the aggregate
 * with all attributes, which is the same as the fully joined star schema
 * with any duplicate rows removed.
 */
public interface Lattice {

    /**
     * Chooses the next un-materialized aggregate with the highest
     * incremental benefit.
     *
     * @param maxCost
     * @param minCostBenefitRatio
     * @param cost Output parameter, is populated with the benefit and the
     *   number of queries which will benefit by materializing the chosen
     *   aggregate
     * @return Chosen aggregate
     */
    AggregateImpl chooseAggregate(
        double maxCost,
        double minCostBenefitRatio,
        Cost cost);

    /**
     * Materializes an aggregate.
     *
     * @param aggregate Aggregate
     */
    void materialize(AggregateImpl aggregate);

    List<AggregateImpl> getMaterializedAggregates();

    /**
     * Returns an object representing the cost of the given aggregate, and
     * benefit of adding it to the current lattice.
     *
     * @param aggregate Aggregate
     * @return Incremental cost/benefit of the aggregate
     */
    Algorithm.CostBenefit costBenefitOf(AggregateImpl aggregate);
}

// End Lattice.java
