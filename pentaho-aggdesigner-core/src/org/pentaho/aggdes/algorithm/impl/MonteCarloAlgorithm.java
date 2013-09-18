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
