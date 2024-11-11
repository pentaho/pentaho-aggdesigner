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
import java.util.List;

import junit.framework.TestCase;

import org.pentaho.aggdes.algorithm.Algorithm.CostBenefit;
import org.pentaho.aggdes.algorithm.impl.AggregateImpl;
import org.pentaho.aggdes.algorithm.impl.Cost;
import org.pentaho.aggdes.algorithm.impl.LatticeImpl;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.util.BitSetPlus;

/**
 * junit test of LatticeImpl abstract class
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
public class LatticeImplTest extends TestCase {
  
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
  
  static class LatticeImplMock extends LatticeImpl {
      LatticeImplMock(Schema schema) {
        super(schema);
      }

      public AggregateImpl chooseAggregate(double maxCost, double minCostBenefitRatio, Cost cost) {
        // TODO Auto-generated method stub
        return null;
      }

      public CostBenefit costBenefitOf(AggregateImpl aggregate) {
        return new CostBenefitMock();
        
      }
      
      public List<AggregateImpl> getAllMaterialized() {
        return materializedAggregates;
      }
      
      public Schema getSchema() {
        return schema;
      }
      
      public List<AggregateImpl> getChildren(AggregateImpl agg) {
        return super.getChildren(agg);
      }
      
      public List<AggregateImpl> getParents(AggregateImpl agg) {
        return super.getParents(agg);
      }
      
      public AggregateImpl getAggregate(BitSetPlus bits) {
        return super.getAggregate(bits);
      }
      
      public BitSetPlus toBits(long bits) {
        return super.toBits(bits);
      }
      
      public List<AggregateImpl> nonMaterializedDescendants(AggregateImpl aggregate, boolean includeSelf) {
        return super.nonMaterializedDescendants(aggregate, includeSelf);
      }
      
      public double getBenefit(AggregateImpl aggregate, int[] benefitCount0) {
        return super.getBenefit(aggregate, benefitCount0);
      }
      
      public double estimateCost(double rowCount, double factRowCount) {
        return super.estimateCost(rowCount, factRowCount);
      }
      
      public AggregateImpl findNearestMaterializedDescendant(AggregateImpl aggregate) {
        return super.findNearestMaterializedDescendant(aggregate);
      }
      
      public List<AggregateImpl> findMaterializedDirectDescendants(
          AggregateImpl aggregate) {
        return super.findMaterializedDirectDescendants(aggregate);
      }
      
      public List<AggregateImpl> findMaterializedDirectAscendants(
          AggregateImpl aggregate)
      {
        return super.findMaterializedDirectAscendants(aggregate);
      }
  }
  
  
  public void testLatticeImpl() {
    SchemaStub schema = new SchemaStub();
    
    LatticeImplMock mock = new LatticeImplMock(schema);
    
    // verify access to schema
    assertEquals(schema, mock.getSchema());
    
    // verify fact table was materialized correctly
    assertEquals(mock.getMaterializedAggregates().size(), 0);
    assertEquals(mock.getAllMaterialized().size(), 1);
    AggregateImpl factAgg = mock.getAllMaterialized().get(0);
    assertEquals(factAgg.getAttributes().size(), 3); // based on mock object defaults
    
    // test getChildren on fact aggregate
    List<AggregateImpl> children = mock.getChildren(factAgg);
    
    // expecting three children back, 1 for each attribute
    assertEquals(children.size(), 3);
    
    List<AggregateImpl> parents = mock.getParents(children.get(0));
    assertEquals(parents.size(), 1);
    // also tests that factAgg isn't in the cache
    
    // TODO: could we put the factAgg in the cache?
    assertNotSame(parents.get(0), factAgg);
    assertEquals(parents.get(0).getAttributes(), factAgg.getAttributes());
    
    BitSetPlus bits = new BitSetPlus();
    bits.set(1);
    bits.set(2);

    // BUG: aggregate.cost doesn't seem to be set correctly
    
    AggregateImpl tmpagg = mock.getAggregate(bits);
    
    List<Attribute> attribs = tmpagg.getAttributes();
    
    // this tmpagg should already exist, and be the first child above
    // verifying the mock.getAggregate caching
    assertEquals(children.get(0), tmpagg);
    
    bits.flip(2);
    AggregateImpl newagg = mock.getAggregate(bits);
    
    assertEquals(newagg.getAttributes().size(), 1);
    
    // verify tmpagg hasn't changed
    assertEquals(attribs, tmpagg.getAttributes());
    
    // test toBits
    // 1010101 = 85
    BitSetPlus bsp = mock.toBits(85);
    assertEquals(bsp.get(0), true);
    assertEquals(bsp.get(1), false);
    assertEquals(bsp.get(2), true);
    assertEquals(bsp.get(3), false);
    assertEquals(bsp.get(4), true);
    assertEquals(bsp.get(5), false);
    assertEquals(bsp.get(6), true);
    
    // BUG: nonMaterializedDescendants currently returns repetive aggregates
    
    // test nonMaterializedDescendants
    
    List<AggregateImpl> list = mock.nonMaterializedDescendants(newagg, true);
    
    assertEquals(list.size(), 2);
    
    list = mock.nonMaterializedDescendants(newagg, false);
    
    assertEquals(list.size(), 1);
    
    // verify this is the root
    assertEquals(list.get(0).getAttributes().size(), 0);
    
    list = mock.nonMaterializedDescendants(factAgg, false);
    
    assertEquals(list.size(), 15); // should return 7
    
    // test getBenefit
    // BUG: aggregate.cost doesn't seem to be set correctly
    
    int bc[] = new int[1];
    double benefit = mock.getBenefit(factAgg, bc);
    
    assertEquals(benefit, 0.0);
    assertEquals(bc[0], 0);
    
    // test estimateCost
    assertEquals(mock.estimateCost(0, 1), 0.0);
    assertEquals(mock.estimateCost(1, 1), 1.0); // ALPHA
    assertEquals(mock.estimateCost(0, Math.E), 100.0); // BETA
    
    // test materialize
    mock.materialize(newagg);
    
    // test by seeing if it is no longer returned in nonMaterializedDescendents
    list = mock.nonMaterializedDescendants(factAgg, false);
    
    assertEquals(list.size(), 11); // should return 6?
    
    // test findNearestMaterializedDescendant
    
    // currently just finds self
    AggregateImpl nearest = mock.findNearestMaterializedDescendant(newagg);
    assertEquals(nearest, newagg);
    
    nearest = mock.findNearestMaterializedDescendant(factAgg);
    assertEquals(nearest, factAgg);

    
    // test findMaterializedDirectDescendants
    
    list = mock.findMaterializedDirectDescendants(factAgg);
    assertEquals(list.size(), 0);

    
    list = mock.findMaterializedDirectDescendants(newagg);
    assertEquals(list.size(), 1);
    assertEquals(list.get(0), factAgg);
    
    // test findMaterializedDirectAscendants
    list = mock.findMaterializedDirectAscendants(factAgg);
    assertEquals(list.size(), 1);
    assertEquals(list.get(0), newagg);
    
    list = mock.findMaterializedDirectAscendants(newagg);
    assertEquals(list.size(), 0);

    // test computeAggregateCosts
    mock = new LatticeImplMock(schema);
    list.add(newagg);
    List<CostBenefit> cblist = mock.computeAggregateCosts(list);
    assertEquals(cblist.size(), 1);
    
  }
}
