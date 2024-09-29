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


package org.pentaho.aggdes.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pentaho.aggdes.ui.model.AggListEvent;
import org.pentaho.aggdes.ui.model.AggListListener;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;

import junit.framework.TestCase;

/**
 * Unit test for AggListImpl
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class AggListImplTest extends TestCase {

  static class TestAggListListener implements AggListListener {

    boolean changed = false;
    List<AggListEvent> events = new ArrayList<AggListEvent>();
    public void listChanged(AggListEvent e) {
      events.add(e);
      changed = true;
    }
    
  };
  
  public void test() {
    
    
    assertTrue( AggListEvent.Type.values() != null );
    assertTrue( AggListEvent.Type.valueOf( "AGG_ADDED" ).toString().equals( "AGG_ADDED" ) );
    
    List<UIAggregate> newAggList = new ArrayList<UIAggregate>();
    UIAggregateImpl agg1 = new UIAggregateImpl();
    newAggList.add(agg1);
    
    AggListImpl aggList1 = new AggListImpl(newAggList);
    assertTrue(aggList1.getAgg(0) == agg1);
    
    
    AggListImpl aggList = new AggListImpl();
    
    TestAggListListener listener = new TestAggListListener();
    
    aggList.addAggListListener(listener);
    
    aggList.addAgg(agg1);
    
    assertTrue(listener.changed);
    assertEquals(listener.events.size(), 1);
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.AGG_ADDED);
    assertEquals(listener.events.get(0).getIndex(), 0);
    
    listener.changed = false;
    listener.events.clear();
    
    aggList.setSelectedIndex(0);
    

    assertTrue(listener.changed);

    assertEquals(2, listener.events.size());
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.SELECTION_CHANGING);
    assertEquals(listener.events.get(0).getIndex(), -1);
    
    assertEquals(listener.events.get(1).getType(), AggListEvent.Type.SELECTION_CHANGED);
    assertEquals(listener.events.get(1).getIndex(), 0);

    assertEquals(aggList.getSelectedIndex(), 0);
    assertEquals(aggList.getSelectedValue(), agg1);
    
    
    aggList.setSelectedIndex(-1);
    
    assertNull(aggList.getSelectedValue());
    
    // exception thrown?
    aggList.setSelectedIndex(1);
    
    listener.changed = false;
    listener.events.clear();
    
    try {
      aggList.aggChanged(new UIAggregateImpl());
      fail();
    } catch (IllegalArgumentException e) {
      // verified
    }
    
    aggList.aggChanged(agg1);
    
    assertEquals(listener.events.size(), 1);
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.AGG_CHANGED);
    assertEquals(listener.events.get(0).getIndex(), 0);
    
    Iterator<UIAggregate> iter = aggList.iterator();
    
    assertNotNull(iter);
    assertEquals(iter.next(), agg1);
    
    assertEquals(aggList.getSize(), 1);
    assertEquals(aggList.getAgg(0), agg1);
    assertNull(aggList.getAgg(-1));
    
    // no exception, just a log warning, verify size doesn't change
    aggList.removeAgg(-1);
    assertEquals(aggList.getSize(), 1);
    aggList.removeAgg(1);
    assertEquals(aggList.getSize(), 1);    
    
    aggList.setSelectedIndex(0);
    listener.changed = false;
    listener.events.clear();
    
    // remove the actual aggregate
    aggList.removeAgg(0);
    
    assertEquals(3, listener.events.size());
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.SELECTION_CHANGING);
    assertEquals(listener.events.get(0).getIndex(), 0);
    
    assertEquals(listener.events.get(1).getType(), AggListEvent.Type.SELECTION_CHANGED);
    assertEquals(listener.events.get(1).getIndex(), -1);
    
    assertEquals(listener.events.get(2).getType(), AggListEvent.Type.AGG_REMOVED);
    assertEquals(listener.events.get(2).getIndex(), 0);
    assertEquals(aggList.getSize(), 0);
    assertEquals(aggList.getSelectedIndex(), -1);
    
    // verify listener can be removed
    
    listener.changed = false;
    
    aggList.removeAggListListener(listener);
    aggList.addAgg(agg1);
    
    assertFalse(listener.changed);
    
    
  }
  public void testMoveAggUpAndDown() {
    // TEST moveAggUp, moveAggDown
    
    List<UIAggregate> newAggList = new ArrayList<UIAggregate>();
    UIAggregateImpl agg1 = new UIAggregateImpl();
    UIAggregateImpl agg2 = new UIAggregateImpl();
    UIAggregateImpl agg3 = new UIAggregateImpl();
    newAggList.add(agg1);
    newAggList.add(agg2);
    newAggList.add(agg3);
    
    AggListImpl aggList1 = new AggListImpl(newAggList);
    TestAggListListener listener = new TestAggListListener();
    aggList1.addAggListListener(listener);
    
    // nothing should happen, already on top
    
    aggList1.moveAggUp(agg1);
    assertEquals(aggList1.getSize(), 3);
    assertEquals(aggList1.getAgg(0), agg1);
    assertFalse(listener.changed);
    
    // now try moving up
    
    aggList1.moveAggUp(agg2);
    assertEquals(aggList1.getSize(), 3);
    assertEquals(aggList1.getAgg(0), agg2);
    assertEquals(aggList1.getAgg(1), agg1);
    assertTrue(listener.changed);
    assertEquals(4, listener.events.size());
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.AGG_CHANGED);
    assertEquals(listener.events.get(1).getType(), AggListEvent.Type.AGG_CHANGED);
    assertEquals(listener.events.get(2).getType(), AggListEvent.Type.SELECTION_CHANGING);
    assertEquals(listener.events.get(3).getType(), AggListEvent.Type.SELECTION_CHANGED);
    
    listener.changed = false;
    listener.events.clear();
    
    // nothing should happen, already on bottom
    
    aggList1.moveAggDown(agg3);
    assertEquals(aggList1.getSize(), 3);
    assertEquals(aggList1.getAgg(2), agg3);
    assertFalse(listener.changed);
    
    // now try moving down

    aggList1.moveAggDown(agg1);
    assertEquals(aggList1.getSize(), 3);
    assertEquals(aggList1.getAgg(1), agg3);
    assertEquals(aggList1.getAgg(2), agg1);
    assertTrue(listener.changed);
    assertEquals(4, listener.events.size());
    assertEquals(listener.events.get(0).getType(), AggListEvent.Type.AGG_CHANGED);
    assertEquals(listener.events.get(1).getType(), AggListEvent.Type.AGG_CHANGED);
    assertEquals(listener.events.get(2).getType(), AggListEvent.Type.SELECTION_CHANGING);
    assertEquals(listener.events.get(3).getType(), AggListEvent.Type.SELECTION_CHANGED);
  }

  public void testCheckAllUnCheckAll() {
    
    List<UIAggregate> newAggList = new ArrayList<UIAggregate>();
    UIAggregateImpl agg1 = new UIAggregateImpl();
    UIAggregateImpl agg2 = new UIAggregateImpl();
    UIAggregateImpl agg3 = new UIAggregateImpl();
    newAggList.add(agg1);
    newAggList.add(agg2);
    newAggList.add(agg3);
    
    AggListImpl aggList1 = new AggListImpl(newAggList);
    TestAggListListener listener = new TestAggListListener();
    aggList1.addAggListListener(listener);
    


    assertTrue(aggList1.getAgg(0).getEnabled());
    assertTrue(aggList1.getAgg(1).getEnabled());
    assertTrue(aggList1.getAgg(2).getEnabled());

    
    aggList1.uncheckAll();
    assertEquals(aggList1.getSize(), 3);
    assertFalse(aggList1.getAgg(0).getEnabled());
    assertFalse(aggList1.getAgg(1).getEnabled());
    assertFalse(aggList1.getAgg(2).getEnabled());
    assertTrue(listener.changed);
    assertEquals(listener.events.size(), 3);
    
    listener.changed = false;
    listener.events.clear();
    
    aggList1.checkAll();
    assertEquals(aggList1.getSize(), 3);
    assertTrue(aggList1.getAgg(0).getEnabled());
    assertTrue(aggList1.getAgg(1).getEnabled());
    assertTrue(aggList1.getAgg(2).getEnabled());
    assertTrue(listener.changed);
    assertEquals(listener.events.size(), 3);

  }

}
