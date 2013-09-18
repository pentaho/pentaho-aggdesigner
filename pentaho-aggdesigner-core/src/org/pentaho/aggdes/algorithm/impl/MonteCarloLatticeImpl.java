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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
package org.pentaho.aggdes.algorithm.impl;

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.util.BitSetPlus;
import org.pentaho.aggdes.util.AggDesUtil;
import org.pentaho.aggdes.algorithm.Algorithm;

import java.util.*;

/**
 * Implementation of the {@link Lattice} data structure for use by
 * the {@link MonteCarloAlgorithm}.
 *
 * @author jhyde
 * @version $Id: MonteCarloLatticeImpl.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Mar 13, 2008
 */
public class MonteCarloLatticeImpl extends LatticeImpl {
    private final int queryCount = 10;
    private final Random random = new Random(12345);

    /**
     * Precomputed list of ancestor ordinals for each attribute. Required by
     * {@link AggregateImpl#hasCompleteAncestors(java.util.List)}.
     */
    private final List<BitSetPlus> ancestorClosure =
        new ArrayList<BitSetPlus>();

    public MonteCarloLatticeImpl(Schema schema) {
        super(schema);

        // Compute ancestor closure
        final int attributeCount = schema.getAttributes().size();
        final Map<Attribute, Integer> attributeOrdinals =
            new IdentityHashMap<Attribute, Integer>();
        for (Attribute attribute : schema.getAttributes()) {
            final int ordinal = ancestorClosure.size();
            attributeOrdinals.put(attribute, ordinal);
            // Initially, the ancestor bit set contains just the attribute
            final BitSetPlus ancestorBitSet = new BitSetPlus(attributeCount);
            ancestorBitSet.set(ordinal);
            ancestorClosure.add(ancestorBitSet);
        }
        // Compute the transitive closure using a fixed-point method.
        int changeCount = 0, lastChangeCount;
        do {
            lastChangeCount = changeCount;
            for (int ordinal = 0; ordinal < attributeCount; ++ordinal) {
                final BitSetPlus bitSet = ancestorClosure.get(ordinal);
                final Attribute attribute =
                    schema.getAttributes().get(ordinal);
                // for each ancestor of this attribute, make sure that all of
                // its ancestors are in the bit set
                for (Attribute ancestorAttribute :
                    attribute.getAncestorAttributes())
                {
                    final int ancestorOrdinal =
                        attributeOrdinals.get(ancestorAttribute);
                    final BitSetPlus ancestorBitSet =
                        ancestorClosure.get(ancestorOrdinal);
                    if (!bitSet.contains(ancestorBitSet)) {
                        ++changeCount;
                        bitSet.or(ancestorBitSet);
                    }
                }
            }
        } while (changeCount > lastChangeCount);

        // Dump the list of attributes and their ancestor closures.
        if (false) {
            for (int ordinal = 0; ordinal < attributeCount; ++ordinal) {
                final Attribute attribute =
                    schema.getAttributes().get(ordinal);
                System.out.println(
                    ordinal + ": " + attribute.getLabel()
                        + ancestorClosure.get(ordinal));
            }
        }
    }

    public AggregateImpl chooseAggregate(
        double maxCost,
        double minCostBenefitRatio,
        Cost cost)
    {
        final int bitCount = schema.getAttributes().size();
        Set<BitSetPlus> queryBitSets = new HashSet<BitSetPlus>();
        Map<AggregateImpl, Cost> aggregateCosts =
            new HashMap<AggregateImpl, Cost>();
        List<AggregateImpl> aggregates = new ArrayList<AggregateImpl>();

        // The maximum cost for any aggregate is a small fraction of the
        // remaining cost. For example, if we have 32 attributes, we'll tend
        // to end up with about 32 aggregates.
        double aggCostLimit = maxCost / (double) bitCount;

        // Run a set of random queries. (Without replacement -- meaning, we
        // never run a query more than once.)

        final int maxRetryCount = 10;
        for (int i = 0, retry = 0; i < queryCount; ++i) {
            boolean b =
                costQuery(
                    minCostBenefitRatio,
                    bitCount,
                    queryBitSets,
                    aggregates,
                    aggregateCosts,
                    aggCostLimit);
            // We've already run that query. Try to find another. If we
            // keep on hitting the same query again and again, give in
            // and allow it.
            if (b && ++retry < maxRetryCount) {
                --i;
            }            
        }

        // Find the aggregate with the greatest benefit.
        AggregateImpl best = null;
        Cost bestCost = null;
        for (AggregateImpl agg : aggregates) {
            Cost aggCost = aggregateCosts.get(agg);
            if (false) {
                System.out.println(
                    "agg=" + agg.getDescription() +
                        ", rows=" + agg.estimateRowCount() +
                        ", cost=" + aggCost);
            }
            if (aggCost.cost > maxCost) {
                continue;
            }
            if (aggCost.benefit <= 0d) {
                continue;
            }
            if (best == null || 
                (aggCost.benefit > bestCost.benefit) ||
                ((aggCost.benefit == bestCost.benefit) && 
                 (aggCost.cost < bestCost.cost))
               ) {
                bestCost = aggCost;
                best = agg;
            }
        }
        if (best != null) {
            cost.copyFrom(bestCost);
        }
        return best;
    }

    private boolean costQuery(
        double minCostBenefitRatio,
        int bitCount,
        Set<BitSetPlus> queryBitSets,
        List<AggregateImpl> aggregates,
        Map<AggregateImpl, Cost> aggregateCosts,
        double aggCostLimit)
    {
        // Generate a random request. Requests are skewed so that ones
        // with k bits are equally likely for all k in [0, bitCount).
        // This reflects real life, where queries aggregated on few
        // attributes are more common (because end-users don't want to
        // see huge results). It's also a waste of time working on
        // queries with a lot of attributes, because an aggregate is
        // unlikely to be able to help these queries (the fact table is
        // almost as good).
        BitSetPlus queryBitSet = new BitSetPlus(bitCount);
        int setBitCount = random.nextInt(bitCount);
        if (setBitCount > 0) {
            double density = 1.0 / (double) setBitCount;
            for (int j = 0; j < bitCount; ++j) {
                if (random.nextDouble() < density) {
                    queryBitSet.set(j);
                }
            }
        }
        if (!queryBitSets.add(queryBitSet)) {
            // We've already run this query.
            return false;
        }

        // Figure out which aggregates this query hits. Breadth-first
        // search, using a queue of aggregates, starting with an
        // aggregate which directly matches the query.
        // Ignores materialized aggregates or their parents.
        AggregateImpl queryAgg = getAggregate(queryBitSet);
        Queue<AggregateImpl> aggQueue = new LinkedList<AggregateImpl>();
        Set<AggregateImpl> seen = new HashSet<AggregateImpl>();
        seen.addAll(materializedAggregates);
        List<AggregateImpl> parents = getParents(queryAgg);
        addAllUnseen(parents, aggQueue, seen);
        while (!aggQueue.isEmpty()) {
            AggregateImpl agg = aggQueue.poll();
            assert seen.contains(agg);
            Cost aggCost = aggregateCosts.get(agg);
            if (aggCost == null) {
                // This is the first query which has hit this particular
                // aggregate. Start its tally.
                double v =
                    estimateCost(
                        agg.rowCount,
                        schema.getStatisticsProvider().getFactRowCount());
                if (v > aggCostLimit) {
                    // Ignore aggregates whose cost is too high. Their
                    // parents will be too high too.
                    continue;
                }
                aggCost = new Cost();
                aggCost.cost = v;
                aggregates.add(agg);
                aggregateCosts.put(agg, aggCost);
            }

            // Only consider instantiating an aggregate if it includes the
            // ancestors of each of its attributes. For example, if an
            // aggregate contains [Month] it must also include [Quarter] and
            // [Year], but it need not contain [Day]. This ensures efficient
            // rollup of hierarchies.
            if (agg.hasCompleteAncestors(ancestorClosure)) {
                // Estimate benefit as the number of rows saved for this
                // query.
                AggregateImpl nearestDescendant =
                    findNearestMaterializedDescendant(agg);
                double benefit =
                    nearestDescendant.rowCount - queryAgg.rowCount;
                assert benefit >= 0.0;
                if (benefit / aggCost.cost < minCostBenefitRatio) {
                    continue;
                }
                aggCost.benefit += benefit;
                aggCost.benefitCount++;
            }

            // Add this agg's parents to the aggQueue.
            addAllUnseen(getParents(agg), aggQueue, seen);
        }
        return true;
    }

    /**
     * Adds all aggregates in <code>list</code> to <code>queue</code>
     * which are not already in the <code>seen</code> set.
     *
     * @param list List of aggregates
     * @param queue Queue
     * @param seen Set of aggregates which have already been seen.
     */
    private void addAllUnseen(
        List<AggregateImpl> list,
        Queue<AggregateImpl> queue,
        Set<AggregateImpl> seen) {
        for (AggregateImpl parent : list) {
            if (seen.add(parent)) {
                queue.add(parent);
            }
        }
    }

    public void materialize(AggregateImpl aggregate) {
        super.materialize(aggregate);
        aggregate.queryLoad = computeQueryLoad(aggregate);
        assert aggregate.queryLoad > 0d : aggregate.queryLoad;
        assert aggregate.queryLoad <= 1d : aggregate.queryLoad;
    }

    public Algorithm.CostBenefit costBenefitOf(AggregateImpl aggregate) {
        // Every query that benefits from this aggregate was previously using
        // the same aggregate (or the fact table). Therefore each query
        // benefits by the same number of rows. The row saving is simply the
        // number of saved rows multiplied by the query load.
        AggregateImpl best = findNearestMaterializedDescendant(aggregate);
        double savedRowCount =
            best.estimateRowCount() - aggregate.estimateRowCount();
        assert savedRowCount > 0;
        materialize(aggregate);
        return new AlgorithmImpl.CostBenefitImpl(
            schema, aggregate, aggregate.queryLoad * savedRowCount);
    }

    /**
     * Returns the probability that an arbitrary query uses a given aggregate.
     *
     * <p>If there are {@code n} bits in the schema, then our query model says
     * that all queries with 0 bits are equally likely, all queries with 1 bits
     * are equally likely, and so forth. In general, there are {@code C(n, k)}
     * queries with {@code k} bits. Therefore the weight of a given query with
     * {@code k} bits is {@code C(n, k) / (n + 1)}.
     *
     * <p>(The query model is somewhat arbitrary. It tries to express the
     * fact that a given query on fewer attributes happens more often a given
     * query with many attributes. The model does not yet recognize that
     * attributes in hierarchies tend to be accessed together, or that certain
     * attributes (e.g. those in the time dimension) are accessed more often
     * than others.)
     *
     * <p>We need to generate the list of queries that would benefit from this
     * aggregate, and compute how many rows are saved for each query. Let's
     * suppose initially that there are no aggregates that are more specific.
     * If the aggregate has {@code b} bits, then there is 1 query with
     * {@code b} bits that can be satisfied (the one that matches the aggregate
     * precisely), (n - b) queries with {@code b + 1} bits, and in
     * general C(n - b, n - r) queries with r bits (b &le; r &le; n);
     * and {@code 2 ^ (n - b)} queries in total.
     *
     * <p>The weights of the queries depend on the level, and the total number
     * of queries in the level. For the level with r bits (b &le; r &le; n),
     * the total number of queries is C(n, r), while the number of queries
     * that can be satisfied by this aggregate is C(b, r - b).
     *
     * <p>For example, suppose n = 10, b = 3 (i.e. the aggregate has 3 out of
     * 10 bits set). There are C(10, 3) = 120 queries at level 3, and only one
     * of them (C(3, 0) = 1) has 3 bits, namely the query that is a direct hit
     * for the aggregate. At level 2 there are C(10, 2) = 45 queries of which
     * C(3, 1) = 3 can use the aggregate.
     *
     * <table>
     * <tr><th>Level</th><th>Total queries</th><th>Satisfiable</th><th>Weight</th></tr>
     * <tr><td>0</td>    <td>C(10, 0)= 1</td><td>C(3, 0) = 1</td><td>1 / 1</td>
     * <tr><td>1</td>    <td>C(10, 1)= 10</td><td>C(3, 1) = 3</td><td>3 / 10</td>
     * <tr><td>2</td>    <td>C(10, 2)= 45</td><td>C(3, 2) = 3</td><td>3 / 45</td>
     * <tr><td>3</td>    <td>C(10, 3)= 120</td><td>C(3, 3) = 1</td><td>1 / 120</td>
     * <tr><td>4 .. 10</td>    <td>C(10, r)</td><td>0</td><td>0</td>
     * </table>
     *
     * <p>Add up all of the level weights and divide by (n + 1) = 11, to get a
     * total weight of 0.1188. 12% of expected queries will use this aggregate.
     *
     * <p>Now let's look at what happens if there are aggregates that are
     * more specific than this one. The queries that use these aggregates will
     * continue to use these aggregates, and thus will not benefit from this
     * aggregate. We therefore subtract the weight of all queries that
     * use these more specific aggregates from the benefit.
     *
     * @param aggregate Aggregate
     * @return proportion of expected queries that will use this aggregate
     */
    private double computeQueryLoad(AggregateImpl aggregate) {
        final int n = schema.getAttributes().size();
        final int b = aggregate.getAttributes().size();
        double load = 0d;
        for (int r = 0; r <= b; r++) {
            // number of queries at this level (i.e. with r attributes) that
            // can be satisfied using the aggregate (i.e. attributes are a
            // subset of the b attributes)
            double x = AggDesUtil.countCombinations(b, r).doubleValue();
            // calculate as proportion of all queries this level, i.e. all
            // queries with r attributes
            x /= AggDesUtil.countCombinations(n, r).doubleValue();
            load += x;
        }
        // divide by number of levels (because each level has equal weight)
        load /= (double) (n + 1);
        // Some queries have a better aggregate (with fewer attributes and
        // therefore fewer rows) and therefore will not fall in this aggregate.
        for (AggregateImpl ascendant
            : findMaterializedDirectAscendants(aggregate)) {
            assert ascendant.queryLoad > 0d
                : "queryLoad should be been initialized on materialize";
            load -= ascendant.queryLoad;
        }
        // The load that falls on this new aggregate is all load that used
        // to fall on other aggregates (with more attributes and therefore more
        // rows)
        for (AggregateImpl descendant
            : findMaterializedDirectDescendants(aggregate)) {
            assert descendant.queryLoad > 0d
                : "queryLoad should be been initialized on materialize";
//            todo: descendant.queryLoad -= something
        }
        return load;
    }
}

// End MonteCarloLatticeImpl.java
