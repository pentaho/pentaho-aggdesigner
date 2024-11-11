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


package org.pentaho.aggdes.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.impl.AggregateImpl;
import org.pentaho.aggdes.algorithm.impl.Cost;
import org.pentaho.aggdes.algorithm.impl.MonteCarloLatticeImpl;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.util.BitSetPlus;

public class MonteCarloLatticeImplTest extends TestCase {
    public void testLatticeImpl() {
      SchemaStub schema = new SchemaStub();

      MonteCarloLatticeImpl lattice = new MonteCarloLatticeImpl(schema);
      
      // TEST chooseAggregate, with zero materialized aggregates
      
      Cost cost = new Cost();
      
      AggregateImpl aggImpl = lattice.chooseAggregate(300000.0, 1.0, cost);
      
      // 
      // The first aggregate should be a single attribute aggregation
      //
      
      assertEquals(aggImpl.getAttributes().size(), 1);
      
      lattice.materialize(aggImpl);

      aggImpl = lattice.chooseAggregate(300000.0, 1.0, cost);

      // 
      // The second aggregate should be a two attribute aggregation
      //
      
      assertEquals(aggImpl.getAttributes().size(), 2);

      lattice.materialize(aggImpl);
      
      aggImpl = lattice.chooseAggregate(300000.0, 1.0, cost);

      // 
      // The third aggregate should be a two attribute aggregation
      //
      
      assertEquals(aggImpl.getAttributes().size(), 2);
      
      // TEST Algorithm.CostBenefit costBenefitOf(AggregateImpl aggregate)
      
      lattice = new MonteCarloLatticeImpl(schema);
      BitSetPlus bsp = new BitSetPlus(3);
      bsp.set(0);
      AggregateImpl aggregate1 = new AggregateImpl(schema, bsp);

      Algorithm.CostBenefit cb = lattice.costBenefitOf(aggregate1);
      
      assertEquals(cb.getLoadTime(), 1000.0);
      assertEquals(cb.getRowCount(), 10.0);
      assertEquals(cb.getSpace(), 10000.0);
      assertEquals(cb.getSavedQueryRowCount(), 330.0);

      bsp = new BitSetPlus(3);
      bsp.set(1);
      AggregateImpl aggregate2 = new AggregateImpl(schema, bsp);

      cb = lattice.costBenefitOf(aggregate2);
      
      assertEquals(cb.getLoadTime(), 1000.0);
      assertEquals(cb.getRowCount(), 10.0);
      assertEquals(cb.getSpace(), 10000.0);
      assertEquals(cb.getSavedQueryRowCount(), 330.0);
      
      bsp = new BitSetPlus(3);
      bsp.set(0);
      bsp.set(2);      
      AggregateImpl aggregate3 = new AggregateImpl(schema, bsp);

      cb = lattice.costBenefitOf(aggregate3);

      assertEquals(cb.getLoadTime(), 1000.0);
      assertEquals(cb.getRowCount(), 100.0);
      assertEquals(cb.getSpace(), 100000.0);
      assertEquals(cb.getSavedQueryRowCount(), 149.99999999999997);
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      cb.describe(pw);
      assertTrue(sw.toString().indexOf("used by 16% of queries") >= 0);
      
      
    }

}
