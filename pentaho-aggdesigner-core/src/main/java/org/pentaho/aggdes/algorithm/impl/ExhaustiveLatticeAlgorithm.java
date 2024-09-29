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
import org.pentaho.aggdes.model.*;
import org.pentaho.aggdes.util.AggDesUtil;

import java.util.*;

/**
 * Implementation of {@link org.pentaho.aggdes.algorithm.Algorithm}
 * that tries all possible sets of
 * {@link org.pentaho.aggdes.model.Aggregate} objects.
 *
 * <p>Expensive in terms of memory and CPU, this algorithm is practical only
 * for small schemas (less than say 15 attributes).
 *
 * @author jhyde
 * @version $Id: ExhaustiveLatticeAlgorithm.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class ExhaustiveLatticeAlgorithm extends AlgorithmImpl {
    public ExhaustiveLatticeAlgorithm()
    {
        super();
    }

    public Result run(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        Progress progress)
    {
        this.schema = schema;
        onStart(parameterValues, progress);

        // Create a lattice, populated with all aggregates.
        Lattice lattice = new ExhaustiveLatticeImpl(schema);
        double remainingCost = schema.getStatisticsProvider().getFactRowCount();
        return runAlgorithm(lattice, remainingCost, 0, Integer.MAX_VALUE);
    }

    public List<CostBenefit> computeAggregateCosts(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        List<Aggregate> aggregateList)
    {
        final ExhaustiveLatticeImpl lattice =
            new ExhaustiveLatticeImpl(schema);
        final List<AggregateImpl> aggregateImplList =
            AggDesUtil.cast(aggregateList);
        return lattice.computeAggregateCosts(aggregateImplList);
    }
}

// End ExhaustiveLatticeAlgorithm.java
