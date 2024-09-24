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

import junit.framework.TestCase;

import org.pentaho.aggdes.ui.form.model.AggregateSummaryModel;

public class AggregateSummaryModelTest extends TestCase {
  public void test() {
    AggregateSummaryModel model = new AggregateSummaryModel();
    
    assertEquals("", model.getSelectedAggregateCount());
    assertEquals("", model.getSelectedAggregateLoadTime());
    assertEquals("", model.getSelectedAggregateRows());
    assertEquals("", model.getSelectedAggregateSpace());

    model.setSelectedAggregateCount("count");
    
    assertEquals("count", model.getSelectedAggregateCount());
    assertEquals("", model.getSelectedAggregateLoadTime());
    assertEquals("", model.getSelectedAggregateRows());
    assertEquals("", model.getSelectedAggregateSpace());

    model.setSelectedAggregateLoadTime("loadtime");
    
    assertEquals("count", model.getSelectedAggregateCount());
    assertEquals("loadtime", model.getSelectedAggregateLoadTime());
    assertEquals("", model.getSelectedAggregateRows());
    assertEquals("", model.getSelectedAggregateSpace());

    model.setSelectedAggregateRows("rows");
    
    assertEquals("count", model.getSelectedAggregateCount());
    assertEquals("loadtime", model.getSelectedAggregateLoadTime());
    assertEquals("rows", model.getSelectedAggregateRows());
    assertEquals("", model.getSelectedAggregateSpace());
    
    model.setSelectedAggregateSpace("space");
    
    assertEquals("count", model.getSelectedAggregateCount());
    assertEquals("loadtime", model.getSelectedAggregateLoadTime());
    assertEquals("rows", model.getSelectedAggregateRows());
    assertEquals("space", model.getSelectedAggregateSpace());
    
    
    
  }
}
