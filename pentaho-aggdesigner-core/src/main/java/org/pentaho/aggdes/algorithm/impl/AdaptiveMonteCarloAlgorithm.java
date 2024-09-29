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
import java.io.PrintWriter;

/**
 * Implementation of {@link org.pentaho.aggdes.algorithm.Algorithm}
 * that runs the {@link MonteCarloAlgorithm Monte Carlo method} repeatedly,
 * tuning the cost limit each time to yield an effective cost limit.
 *
 * <p>The Monte Carlo algorithm tends to be most effective when the target
 * cost is close to the actual cost. By running the algorithm repeatedly,
 * we can determine a realistic cost.
 *
 * @author jhyde
 * @version $Id: AdaptiveMonteCarloAlgorithm.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class AdaptiveMonteCarloAlgorithm extends AlgorithmImpl {
    private double costLimit;
    private int aggregateLimit;

    public AdaptiveMonteCarloAlgorithm()
    {
        super();
    }

    public Result run(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        Progress progress)
    {
        onStart(parameterValues, progress);
        this.schema = schema;
        final Double d = (Double) parameterValues.get(ParameterEnum.costLimit);
        this.costLimit = d == null ? Double.MAX_VALUE : d;
        final Integer i =
            (Integer) parameterValues.get(ParameterEnum.aggregateLimit);
        this.aggregateLimit = i == null ? Integer.MAX_VALUE : i;
        return adaptiveMonteCarlo();
    }

    private Result adaptiveMonteCarlo()
    {
        PrintWriter pw = new PrintWriter(System.out);
        double localCostLimit = 1.0;
        List<Result> results = new ArrayList<Result>();
        List<Double> costLimits = new ArrayList<Double>();
        final double factRowCount =
            schema.getStatisticsProvider().getFactRowCount();

        // Run the algorithm a few times, each time constrained by a cost limit.
        // The limit is initially quite low, which ensures that we do not waste
        // time considering expensive aggregates (that is, aggregates with many
        // rows). Each time we run the algorithm, we move the cost limit up.
        //
        // We terminate when the benefit stops improving in line with the cost.
        while (true) {
            if (checkCancelTimeout()) {
                break;
            }
            pw.println("Try with cost=" + localCostLimit);
            final double costBenefitRatio = 1.0;
            ResultImpl result =
                runAlgorithm(
                    new MonteCarloLatticeImpl(schema),
                    localCostLimit,
                    costBenefitRatio,
                    aggregateLimit);
            result.describe(pw);
            pw.flush();
            costLimits.add(localCostLimit);
            results.add(result);
            if (localCostLimit > factRowCount &&
                result.benefit > result.cost * .1)
            {
                break;
            } else if (localCostLimit >= costLimit) {
                break;
            } else {
                localCostLimit *= 5.0;
            }
        }
        // Assume that the last result is the best.
        return results.isEmpty()
            ? null
            : results.get(results.size() - 1);
    }

    public List<CostBenefit> computeAggregateCosts(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        List<Aggregate> aggregateList)
    {
        final LatticeImpl lattice =
            new MonteCarloLatticeImpl(schema);
        final List<AggregateImpl> aggregateImplList =
            AggDesUtil.cast(aggregateList);
        return lattice.computeAggregateCosts(aggregateImplList);
    }
}

// AdaptiveMonteCarloAlgorithm.java
