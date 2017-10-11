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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.algorithm.impl;

import org.pentaho.aggdes.model.*;
import org.pentaho.aggdes.util.BitSetPlus;
import org.pentaho.aggdes.algorithm.Algorithm;

import java.util.*;

/**
 * Abstract implementation of the Lattice data structure.
 */
public abstract class LatticeImpl implements Lattice {
    protected final Schema schema;
    private Map<BitSetPlus, AggregateImpl> mapBitsToAggregate =
            new HashMap<BitSetPlus, AggregateImpl>();

    /**
     * List of all materialized tables.
     */
    protected List<AggregateImpl> materializedAggregates =
            new ArrayList<AggregateImpl>();

    /**
     * How heavily to weight storage in the cost function,
     * {@link #estimateCost(double, double)}.
     */
    private static final double Alpha = 1d;

    /**
     * How heavily to weight computational effort, and the cost of loading the
     * aggregate table, in the cost function,
     * {@link #estimateCost(double, double)}.
     */
    private static final double Beta = 100d;

    /**
     * Creates a lattice.
     *
     * <p>The lattice is fully populated with aggregates.
     * The number of tables is exponential -- not advised if you have a
     * large number of attributes.
     *
     * <p>The cost of each aggregate is the cost of scanning the fact
     * table.
     *
     * @param schema Schema
     */
    protected LatticeImpl(Schema schema) {
        this.schema = schema;

        // the fact table has all attributes
        BitSetPlus allBits = new BitSetPlus();
        allBits.set(0, schema.getAttributes().size());
        AggregateImpl fact = new AggregateImpl(this.schema, allBits);
        fact.rowCount = schema.getStatisticsProvider().getFactRowCount();

        // the fact table is always materialized, but is not registered
        // in mapBitsToAggregate because it is not an aggregate table
        materialize(fact);
    }

    public List<AggregateImpl> getMaterializedAggregates() {
        // return the list minus the 0th element, which is the fact table
        return materializedAggregates.subList(1, materializedAggregates.size());
    }

    /**
     * Returns a list of aggregates which can be computed from a given
     * aggregate by dropping one attribute.
     *
     * @param aggregate Aggregate
     * @return List of aggregates that have one fewer attribute
     */
    protected List<AggregateImpl> getChildren(
        AggregateImpl aggregate)
    {
        BitSetPlus bits = aggregate.bits;
        List<AggregateImpl> list = new ArrayList<AggregateImpl>();
        for (int i = bits.nextSetBit(0);
             i >= 0;
             i = bits.nextSetBit(i + 1)) {
            bits.clear(i);
            AggregateImpl child = getAggregate(bits);
            list.add(child);
            bits.set(i);
        }
        return list;
    }

    /**
     * Returns a list of aggregates which can be computed from a given
     * aggregate by adding one attribute. Each parent has more rows than
     * the given aggregate.
     *
     * @param aggregate Aggregate
     * @return List of aggregates that have one more attribute
     */
    protected List<AggregateImpl> getParents(
        AggregateImpl aggregate) {
        BitSetPlus bits = aggregate.bits;
        List<AggregateImpl> list = new ArrayList<AggregateImpl>();
        for (int i = bits.nextClearBit(0);
             i >= 0 && i < schema.getAttributes().size();
             i = bits.nextClearBit(i + 1)) {
            bits.set(i);
            AggregateImpl parent = getAggregate(bits);
            list.add(parent);
            bits.clear(i);
        }
        return list;
    }

    protected AggregateImpl getAggregate(BitSetPlus bits) {
        AggregateImpl aggregate = mapBitsToAggregate.get(bits);
        if (aggregate == null) {
            BitSetPlus clonedBits = (BitSetPlus) bits.clone();
            aggregate = new AggregateImpl(schema, clonedBits);
            aggregate.cost = schema.getStatisticsProvider().getRowCount(aggregate.getAttributes());
            mapBitsToAggregate.put(clonedBits, aggregate);
        }
        return aggregate;
    }

    protected BitSetPlus toBits(long i) {
        BitSetPlus bits = new BitSetPlus();
        int j = 0;
        long k = i;
        while (k != 0) {
            if ((k & 1) == 1) {
                bits.set(j);
            }
            ++j;
            k >>= 1;
        }
        return bits;
    }

    /**
     * Returns a list of the non-materialized descendants of an aggregate.
     * BUG: warning, this method returns redundant child aggregates
     *
     * @param aggregate Aggregate
     * @param includeSelf Whether to include aggregate in the list
     * @return List of descendants that have not been materialized
     */
    protected List<AggregateImpl> nonMaterializedDescendants(
        AggregateImpl aggregate,
        boolean includeSelf)
    {
        List<AggregateImpl> list = new ArrayList<AggregateImpl>();

        // Add aggregate to the list, to seed it.
        list.add(aggregate);

        for (int j = 0; j < list.size(); ++j) {
            // Choose the next element in the list.
            AggregateImpl childAggregate = list.get(j);
            if (j == 0 && !includeSelf) {
                list.remove(j);
                includeSelf = true;
                --j;
            }
            appendNonMaterializedChildren(childAggregate, list);
        }

        return list;
    }

    /**
     * Appends the non-materialized children of a given aggregate to the
     * list of aggregates.
     * @param aggregate Aggregate
     * @param list List of aggregates to append to
     */
    private void appendNonMaterializedChildren(
        AggregateImpl aggregate,
        List<AggregateImpl> list)
    {
        BitSetPlus bits = aggregate.bits;
        for (int i = bits.nextSetBit(0);
             i >= 0;
             i = bits.nextSetBit(i + 1)) {
            bits.clear(i);
            AggregateImpl child = getAggregate(bits);
            if (!child.materialized) {
                list.add(child);
            }
            bits.set(i);
        }
    }

    /**
     * Returns the benefit of materializing an aggregate.
     */
    protected double getBenefit(AggregateImpl aggregate, int[] benefitCount0) {
        double costSaving = 0;
        int benefitCount = 0;
        double aggregateCost = aggregate.estimateRowCount();
        for (AggregateImpl child : nonMaterializedDescendants(aggregate, true)) {
            if (aggregateCost < child.cost) {
                costSaving += (child.cost - aggregateCost);
                ++benefitCount;
            }
        }
        // Cost is [some function of] storage and computation time.
        double cost = estimateCost(
            aggregate.estimateRowCount(),
            schema.getStatisticsProvider().getFactRowCount());
        double benefit = costSaving / cost;
        benefitCount0[0] = benefitCount;
        return benefit;
    }

    /**
     * Returns an estimate of the cost, in terms of storage and computation
     * time, of creating an aggregate table.
     *
     * @param rowCount Number of rows in aggregate table (determines the
     *   amount of storage required for the aggregate table)
     * @param factRowCount Number of rows in the fact table (determines
     *   the amount of computational effort to create the aggregate table)
     * @return A number representing the cost of creating this aggregate
     *   table
     */
    protected double estimateCost(double rowCount, double factRowCount) {
        return Alpha * rowCount +
            Beta * Math.log(factRowCount);
    }

    public void materialize(AggregateImpl aggregate) {
        assert !aggregate.materialized;
        aggregate.materialized = true;
        materializedAggregates.add(aggregate);
    }

    /**
     * Finds the aggregate which is materialized, is a descendant of
     * <code>aggregate</code> (that is, it has a superset of its
     * attributes), and has the fewest rows among such aggregates.
     * If there is no such aggregate, returns the fact table, so this method
     * never returns null.
     * This helps us compute the benefit of materializing
     * <code>aggregate</code>.
     *
     * @param aggregate Aggregate
     * @return nearest materialized aggregate that is a descendant of the given
     *   aggregate
     */
    protected AggregateImpl findNearestMaterializedDescendant(
        AggregateImpl aggregate)
    {
        AggregateImpl best = null;
        for (AggregateImpl materializedAggregate : materializedAggregates) {
            if (materializedAggregate.bits.contains(aggregate.bits)) {
                if (best == null ||
                    materializedAggregate.rowCount < best.rowCount) {
                    best = materializedAggregate;
                }
            }
        }
        assert best != null;
        return best;
    }

    /**
     * Returns a list of aggregates that are materialized, are a descendant
     * of {@code aggregate} (that is, a strict superset of its attributes),
     * and there is no intervening materialized aggregate.
     *
     * @param aggregate Aggregate
     * @return list of materialized aggregates that are an ascendant of the
     *   given aggregate
     */
    protected List<AggregateImpl> findMaterializedDirectDescendants(
        AggregateImpl aggregate)
    {
        List<AggregateImpl> list = new ArrayList<AggregateImpl>();
        for (AggregateImpl materializedAggregate : materializedAggregates) {
            if (materializedAggregate.bits.contains(aggregate.bits)
                && !materializedAggregate.bits.equals(aggregate.bits)) {
                list.add(materializedAggregate);
            }
        }
        // Remove aggregates from the list that are subsets of other
        // aggregates in the list
        iLoop:
        for (int i = 0; i < list.size(); i++) {
            AggregateImpl aggregate1 = list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (i == j) {
                    continue;
                }
                AggregateImpl aggregate2 = list.get(j);
                if (aggregate2.bits.contains(aggregate1.bits)) {
                    assert !aggregate2.bits.equals(aggregate1.bits) :
                        "materialized aggs should be unique";
                    list.remove(i);
                    --i;
                    continue iLoop;
                }
            }
        }
        return list;
    }

    /**
     * Returns a list of aggregates that are materialized, are an ascendant
     * of {@code aggregate} (that is, a strict subset of its attributes),
     * and there is no intervening materialized aggregate.
     *
     * @param aggregate Aggregate
     * @return nearest materialized aggregate that is a descendant of the given
     *   aggregate
     */
    protected List<AggregateImpl> findMaterializedDirectAscendants(
        AggregateImpl aggregate)
    {
        List<AggregateImpl> list = new ArrayList<AggregateImpl>();
        for (AggregateImpl materializedAggregate : materializedAggregates) {
            if (aggregate.bits.contains(materializedAggregate.bits)
                && !aggregate.bits.equals(materializedAggregate.bits)) {
                list.add(materializedAggregate);
            }
        }
        // Remove aggregates from the list that are supersets of other
        // aggregates in the list
        iLoop:
        for (int i = 0; i < list.size(); i++) {
            AggregateImpl aggregate1 = list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (i == j) {
                    continue;
                }
                AggregateImpl aggregate2 = list.get(j);
                if (aggregate1.bits.contains(aggregate2.bits)) {
                    assert !aggregate1.bits.equals(aggregate2.bits) :
                        "materialized aggs should be unique";
                    list.remove(i);
                    --i;
                    continue iLoop;
                }
            }
        }
        return list;
    }

    /**
     * Helper method for
     * {@link org.pentaho.aggdes.algorithm.Algorithm#computeAggregateCosts}.
     * The lattice must be empty when this method is called.
     *
     * @param aggregateList List of aggregates
     * @return List of cost/benefit metrics for each aggregate
     */
    public List<Algorithm.CostBenefit> computeAggregateCosts(
        List<AggregateImpl> aggregateList)
    {
        // When called, only the fact table is materialized.
        assert materializedAggregates.size() == 1;
        final List<Algorithm.CostBenefit> list =
            new ArrayList<Algorithm.CostBenefit>(aggregateList.size());
        for (AggregateImpl aggregate : aggregateList) {
            aggregate.materialized = false;
            // note: it's the responsibility of costBenefitOf to 
            // materialize the aggregate
            list.add(costBenefitOf(aggregate));
        }
        return list;
    }
}

// End LatticeImpl.java
