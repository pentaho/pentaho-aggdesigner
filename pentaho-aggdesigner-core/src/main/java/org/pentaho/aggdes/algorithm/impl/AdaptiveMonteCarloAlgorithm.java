/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

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
