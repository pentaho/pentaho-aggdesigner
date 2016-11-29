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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.Progress;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.Algorithm.CostBenefit;
import org.pentaho.aggdes.algorithm.impl.AggregateImpl;
import org.pentaho.aggdes.algorithm.impl.AlgorithmImpl;
import org.pentaho.aggdes.algorithm.impl.Cost;
import org.pentaho.aggdes.algorithm.impl.Lattice;
import org.pentaho.aggdes.algorithm.impl.ResultImpl;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
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
      // TODO Auto-generated method stub
      return null;
    }

    public List<AggregateImpl> getMaterializedAggregates() {
      return materialized;
    }

    public void materialize(AggregateImpl aggregate) {
      materialized.add(aggregate);
      
    }
    
  }
  
  static class AlgorithmImplStub extends AlgorithmImpl {

    boolean computeAggCostsCalled = false;
    public List<CostBenefit> computeAggregateCosts(Schema schema,
        Map<Parameter, Object> parameterValues, List<Aggregate> aggregateList) {
      computeAggCostsCalled = true;
      return new ArrayList<CostBenefit>();
    }

    public Result run(Schema schema, Map<Parameter, Object> parameterValues, Progress progress) {
      return null;
    }
    
    public ResultImpl runAlgorithm(
        Lattice lattice,
        double costLimit,
        double minCostBenefitRatio,
        int aggregateLimit)
    {
      return super.runAlgorithm(lattice, costLimit, minCostBenefitRatio, aggregateLimit);
    }
    
    public void onStart(
        Map<Parameter, Object> parameterValues,
        Progress progress)
    {
      super.onStart(parameterValues, progress);
    }
  }
  
  public void test() {
    AlgorithmImplStub algorithmImplStub = new AlgorithmImplStub();
    
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
    ResultImpl results = algorithmImplStub.runAlgorithm(lattice, 0.0, 0.0, 0);
    assertEquals(results.getAggregates().size(), 0);
    assertEquals(results.getCostBenefits().size(), 0);
    assertEquals(progress.messageCount, 1);
    
    // TEST runAlgorithm with no available aggregates in lattice stub,
    // should return no results
    
    progress = new ProgressStub();
    lattice = new LatticeStub();
    parameterValues = new HashMap<Parameter, Object>();
    algorithmImplStub.onStart(parameterValues, progress);
    results = algorithmImplStub.runAlgorithm(lattice, 0.0, 0.0, 0);
    assertEquals(results.getAggregates().size(), 0);
    assertEquals(results.getCostBenefits().size(), 0);
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
    
    results = algorithmImplStub.runAlgorithm(lattice, 1200.0, Double.MAX_VALUE, Integer.MAX_VALUE);
    
    assertEquals(results.getAggregates().size(), 2);
    assertEquals(results.getAggregates().get(0), agg1);
    assertEquals(results.getAggregates().get(1), agg2);
    
    // modify cost benefit for now
    results.getCostBenefits().add(new CostBenefitMock());
    results.getCostBenefits().add(new CostBenefitMock());
    
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    results.describe(pw);
    
    assertTrue(sw.toString().indexOf("Cost/benefit ratio: 0.125") >= 0);
    
    assertEquals(lattice.toChoose.size(), 0);
    assertEquals(lattice.lastMaxCost, 1050.0);
    assertEquals(lattice.lastMinCostBenefitRatio, 1.7976931348623157E308);
    assertTrue(algorithmImplStub.computeAggCostsCalled);
    
    assertEquals(progress.messageCount, 0);

    // 
    
  }

}
