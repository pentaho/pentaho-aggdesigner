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

package org.pentaho.aggdes.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Progress;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.Algorithm.CostBenefit;
import org.pentaho.aggdes.algorithm.impl.AggregateImpl;
import org.pentaho.aggdes.algorithm.impl.AlgorithmImpl;
import org.pentaho.aggdes.algorithm.impl.Cost;
import org.pentaho.aggdes.algorithm.impl.Lattice;
import org.pentaho.aggdes.algorithm.impl.LatticeImpl;
import org.pentaho.aggdes.algorithm.impl.MonteCarloLatticeImpl;
import org.pentaho.aggdes.algorithm.impl.ResultImpl;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dimension;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.StatisticsProvider;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.util.AggDesUtil;
import org.pentaho.aggdes.util.BitSetPlus;

import junit.framework.TestCase;

public class AlgorithmImplTest extends TestCase {

  static class CostBenefitMock implements CostBenefit {

    public void describe(PrintWriter pw) {
      // TODO Auto-generated method stub

    }

    public double getLoadTime() {
      // TODO Auto-generated method stub
      return 0;
    }

    public double getRowCount() {
      // TODO Auto-generated method stub
      return 0;
    }

    public double getSavedQueryRowCount() {
      // TODO Auto-generated method stub
      return 0;
    }

    public double getSpace() {
      // TODO Auto-generated method stub
      return 0;
    }

  }

  static class ProgressStub implements Progress {

    public int messageCount;

    public void report(String message, double complete) {
      messageCount++;
    }
  }

  static class LatticeStub implements Lattice {
    List<AggregateImpl> materialized = new ArrayList<AggregateImpl>();
    List<AggregateImpl> toChoose = new ArrayList<AggregateImpl>();
    List<Cost> toChooseCosts = new ArrayList<Cost>();
    double lastMaxCost;
    double lastMinCostBenefitRatio;

    @Override public Lattice copy() {
      return new LatticeStub();
    }

    public AggregateImpl chooseAggregate(double maxCost, double minCostBenefitRatio, Cost cost) {
      lastMaxCost = maxCost;
      lastMinCostBenefitRatio = minCostBenefitRatio;
      if (toChoose.size() > 0) {
        Cost newCost = toChooseCosts.remove(0);
        cost.copyFrom(newCost);
        return toChoose.remove(0);
      }
      return null;
    }

    public CostBenefit costBenefitOf(AggregateImpl aggregate) {
      return new CostBenefitMock();
    }

    public List<AggregateImpl> getMaterializedAggregates() {
      return materialized;
    }

    public void materialize(AggregateImpl aggregate) {
      materialized.add(aggregate);

    }

  }

  static class AlgorithmImplStub extends AlgorithmImpl {
    private final Lattice emptyLattice;
    boolean computeAggCostsCalled = false;

    protected AlgorithmImplStub(Lattice lattice) {
      super();
      this.emptyLattice = lattice.copy();
    }

    public List<CostBenefit> computeAggregateCosts(Schema schema,
        Map<Parameter, Object> parameterValues, List<Aggregate> aggregateList) {
      computeAggCostsCalled = true;
      final List<AggregateImpl> aggregateImplList = AggDesUtil.cast(aggregateList);
      return LatticeImpl.computeAggregateCosts(emptyLattice.copy(), aggregateImplList);
    }

    public Result run(Schema schema, Map<Parameter, Object> parameterValues, Progress progress) {
      return null;
    }

    @Override // and make public
    public ResultImpl runAlgorithm(
        Lattice lattice,
        double costLimit,
        double minCostBenefitRatio,
        int aggregateLimit)
    {
      return super.runAlgorithm(lattice, costLimit, minCostBenefitRatio, aggregateLimit);
    }

    @Override // and make public
    public void onStart(
        Map<Parameter, Object> parameterValues,
        Progress progress)
    {
      super.onStart(parameterValues, progress);
    }
  }

  public void test() {
    AlgorithmImplStub algorithmImplStub = new AlgorithmImplStub(new LatticeStub());

    // TEST name

    assertEquals(algorithmImplStub.getName(), "AlgorithmImplTest$AlgorithmImplStub");

    // TEST params

    List<Parameter> params = algorithmImplStub.getParameters();
    assertEquals(params.size(), 3);

    assertEquals(params.get(0).getName(), "timeLimitSeconds");
    assertEquals(params.get(0).getDescription(), "Maximum time, in seconds, to run the algorithm. After this time, the algorithm returns the best solution it has found so far.");
    assertEquals(params.get(0).getType(), Parameter.Type.INTEGER);
    assertEquals(params.get(0).isRequired(), false);

    assertEquals(params.get(1).getName(), "aggregateLimit");
    assertEquals(params.get(1).getDescription(), "Maximum number of aggregates to create");
    assertEquals(params.get(1).getType(), Parameter.Type.INTEGER);

    assertEquals(params.get(2).getName(), "costLimit");
    assertEquals(params.get(2).getDescription(), "Maximum total cost of the aggregates produced.");
    assertEquals(params.get(2).getType(), Parameter.Type.DOUBLE);

    // TEST createAggregate

    SchemaStub schema = new SchemaStub();

    List<Attribute> attributeList = new ArrayList<Attribute>();
    attributeList.add(schema.getAttributes().get(0));
    Aggregate agg = algorithmImplStub.createAggregate(schema, attributeList);

    assertEquals(agg.getAttributes().size(), 1);
    assertEquals(agg.getAttributes().get(0), schema.getAttributes().get(0));

    assertEquals(agg.getMeasures().size(), 1);

    // TEST runAlgorithm with cancel already set
    ProgressStub progress = new ProgressStub();
    LatticeStub lattice = new LatticeStub();
    Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
    algorithmImplStub.onStart(parameterValues, progress);
    algorithmImplStub.cancel();
    ResultImpl result = algorithmImplStub.runAlgorithm(lattice, 0.0, 0.0, 0);
    assertEquals(result.getAggregates().size(), 0);
    assertEquals(result.getCostBenefits().size(), 0);
    assertEquals(progress.messageCount, 1);

    // TEST runAlgorithm with no available aggregates in lattice stub,
    // should return no results

    progress = new ProgressStub();
    lattice = new LatticeStub();
    parameterValues = new HashMap<Parameter, Object>();
    algorithmImplStub.onStart(parameterValues, progress);
    result = algorithmImplStub.runAlgorithm(lattice, 0.0, 0.0, 0);
    assertEquals(result.getAggregates().size(), 0);
    assertEquals(result.getCostBenefits().size(), 0);
    assertEquals(progress.messageCount, 0);

    // TEST runAlgorithm with two aggregate suggestions

    progress = new ProgressStub();
    lattice = new LatticeStub();

    BitSetPlus bits = new BitSetPlus();
    bits.set(1);
    bits.set(2);
    AggregateImpl agg1 = new AggregateImpl(schema, bits);
    lattice.toChoose.add(agg1);
    Cost cost1 = new Cost();
    cost1.benefit = 800.0;
    cost1.cost = 100.0;
    lattice.toChooseCosts.add(cost1);

    bits = new BitSetPlus();
    bits.set(2);
    Cost cost2 = new Cost();
    cost2.benefit = 400.0;
    cost2.cost = 50.0;
    AggregateImpl agg2 = new AggregateImpl(schema, bits);
    lattice.toChoose.add(agg2);
    lattice.toChooseCosts.add(cost2);

    lattice.materialized.clear();
    algorithmImplStub.computeAggCostsCalled = false;
    parameterValues = new HashMap<Parameter, Object>();
    algorithmImplStub.onStart(parameterValues, progress);

    result = algorithmImplStub.runAlgorithm(lattice, 1200.0, Double.MAX_VALUE, Integer.MAX_VALUE);

    assertEquals(result.getAggregates().size(), 2);
    assertEquals(result.getAggregates().get(0), agg1);
    assertEquals(result.getAggregates().get(1), agg2);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    result.describe(pw);

    assertTrue(sw.toString().indexOf("Cost/benefit ratio: 0.125") >= 0);

    assertEquals(lattice.toChoose.size(), 0);
    assertEquals(lattice.lastMaxCost, 1050.0);
    assertEquals(lattice.lastMinCostBenefitRatio, 1.7976931348623157E308);
    assertTrue(algorithmImplStub.computeAggCostsCalled);

    assertEquals(progress.messageCount, 0);

    //

  }

  /** Tests a lattice with 10 attributes and 3 measures on a fact table with
   * 1 million rows. The attributes are uniformly distributed, independent, and
   * have a small number of distinct values, all different). */
  public void testTenDistinctAttributes() {
    final int A = 10;
    final int M = 3;
    final int F = 1000000;

    final SchemaStub schema = new SchemaStub() {
      @Override protected StatisticsProvider init(List<Attribute> attributes,
          List<Measure> measures, List<Dimension> dimensions) {
        final TableStub table = new TableStub("t", null);
        BigInteger c = BigInteger.ONE;
        for (int i = 0; i < A; i++) {
          c = c.nextProbablePrime();
          attributes.add(new MyAttribute("a" + c, table, c.intValue()));
        }
        for (int i = 0; i < M; i++) {
          final String label = "m" + i;
          measures.add(new MeasureStub(1, label, "int", label, table));
        }
        return new StatisticsProvider() {
          @Override
          public double getFactRowCount() {
            return F;
          }

          @Override
          public double getRowCount(List<Attribute> attributes) {
            // From http://math.stackexchange.com/questions/72223/finding-expected-number-of-distinct-values-selected-from-a-set-of-integers
            // the expected number of distinct values when choosing p values
            // with replacement from n integers is n . (1 - ((n - 1) / n) ^ p).
            //
            // If we have several uniformly distributed attributes A1 ... Am
            // with N1 ... Nm distinct values, they behave as one uniformly
            // distributed attribute with N1 * ... * Nm distinct values.
            BigInteger n = BigInteger.ONE;
            for (Attribute attribute : attributes) {
              final int cardinality = ((MyAttribute) attribute).cardinality;
              if (cardinality > 1) {
                n = n.multiply(BigInteger.valueOf(cardinality));
              }
            }
            final double nn = n.doubleValue();
            final double f = getFactRowCount();
            final double a = (nn - 1d) / nn;
            if (a == 1d) {
              // If nn is large, a under-flows, but we know the answer is the
              // number of rows in the fact table.
              return f;
            }
            // TODO: Investigate using Math.expm1.
            final double v = nn * (1d - Math.pow(a, f));
            // Cap at fact-row-count, because numerical artifacts can cause it
            // to go a few percent over.
            return Math.min(v, f);
          }

          @Override
          public double getSpace(List<Attribute> attributes) {
            return attributes.size();
          }

          @Override
          public double getLoadTime(List<Attribute> attributes) {
            return getSpace(attributes) * getRowCount(attributes);
          }
        };
      }
    };

    ProgressStub progress = new ProgressStub();
    Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();
    final Lattice lattice = new MonteCarloLatticeImpl(schema);
    final AlgorithmImplStub algorithm = new AlgorithmImplStub(lattice);
    algorithm.onStart(parameterValues, progress);
    final ResultImpl result = algorithm.runAlgorithm(lattice, F, 50d, 10);

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    result.describe(pw);
    pw.flush();

    assertEquals(9, result.getAggregates().size(), 0);
    final String s = sw.toString();
    assertTrue(s, toLinux(s).startsWith("AggregateTable: a2, a3, a5, a13, a29; \n"
        + "11310 rows, 56550 bytes, 56550 load cost, 164781 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a7, a11, a13; \n"
        + "6006 rows, 30030 bytes, 30030 load cost, 165665 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a5, a13, a19; \n"
        + "7410 rows, 37050 bytes, 37050 load cost, 165431 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a11, a13, a17; \n"
        + "14586 rows, 72930 bytes, 72930 load cost, 164235 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a7, a11, a23; \n"
        + "10626 rows, 53130 bytes, 53130 load cost, 164895 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a5, a7, a11, a17; \n"
        + "13090 rows, 65450 bytes, 65450 load cost, 164485 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a5, a7, a11, a19; \n"
        + "14630 rows, 73150 bytes, 73150 load cost, 164228 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a5, a19, a23; \n"
        + "13110 rows, 65550 bytes, 65550 load cost, 164481 query rows saved, used by 16% of queries\n"
        + "AggregateTable: a2, a3, a5, a17, a19; \n"
        + "9690 rows, 48450 bytes, 48450 load cost, 165051 query rows saved, used by 16% of queries\n"));
  }

  public static String toLinux(String s) {
    return s.replaceAll("\r\n", "\n");
  }

  private static class MyAttribute extends SchemaStub.AttributeStub {
    private final int cardinality;

    public MyAttribute(String label, SchemaStub.TableStub table, int cardinality) {
      super(1, label, "string", label, table);
      this.cardinality = cardinality;
    }
  }
}
