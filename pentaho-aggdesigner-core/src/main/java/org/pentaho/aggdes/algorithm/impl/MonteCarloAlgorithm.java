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

import java.util.Map;
import java.util.List;

/**
 * Implementation of {@link org.pentaho.aggdes.algorithm.Algorithm} that
 * measures the effectiveness of a set of
 * {@link org.pentaho.aggdes.model.Aggregate} objects by generating a
 * set of representative sample queries.
 *
 * <p>Using a random set of queries is much cheaper than generating the whole
 * set, as done by {@link ExhaustiveLatticeAlgorithm}, but is not much less
 * accurate.</p>
 *
 * @author jhyde
 * @version $Id: MonteCarloAlgorithm.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class MonteCarloAlgorithm extends AlgorithmImpl {
    public MonteCarloAlgorithm()
    {
    }

    public Result run(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        Progress progress)
    {
        this.schema = schema;
        onStart(parameterValues, progress);

        // Create a lattice, populated with all aggregates.
        Lattice lattice = new MonteCarloLatticeImpl(this.schema);
        double remainingCost = schema.getStatisticsProvider().getFactRowCount();
        return runAlgorithm(lattice, remainingCost, 0, Integer.MAX_VALUE);
    }

    public List<CostBenefit> computeAggregateCosts(
        Schema schema,
        Map<Parameter, Object> parameterValues,
        List<Aggregate> aggregateList)
    {
        this.schema = schema;
        final LatticeImpl lattice =
            new MonteCarloLatticeImpl(schema);
        final List<AggregateImpl> aggregateImplList =
            AggDesUtil.cast(aggregateList);
        return lattice.computeAggregateCosts(aggregateImplList);
    }
}

// MonteCarloAlgorithm.java
