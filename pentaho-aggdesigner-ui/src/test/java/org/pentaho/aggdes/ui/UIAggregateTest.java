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


package org.pentaho.aggdes.ui;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;

import junit.framework.TestCase;

public class UIAggregateTest extends TestCase {

  static class OutputStub implements Output {
    public Aggregate getAggregate() {
      return null;
    }
  }

  public void testUIAggregateImpl() {
    UIAggregateImpl impl = new UIAggregateImpl();

    assertNotNull(impl.getAttributes());
    assertNotNull(impl.getMeasures());
    assertEquals(impl.getAttributes().size(), 0);
    assertEquals(impl.getMeasures().size(), 0);
    assertEquals(impl.getName(), "");
    assertEquals(impl.getCandidateTableName(), "");
    assertEquals(impl.getDescription(), "");
    assertTrue(impl.getEnabled());
    assertEquals(impl.estimateRowCount(), 0.0);
    assertEquals(impl.estimateSpace(), 0.0);
    assertNull(impl.getOutput());
    assertFalse(impl.isAlgoAgg());

    // twiddle bits and verify results

    impl.setAttributes(null);
    assertNull(impl.getAttributes());

    impl.setMeasures(null);
    assertNull(impl.getMeasures());

    impl.setName("test_name");
    assertEquals("test_name", impl.getName());
    assertEquals("test_name", impl.getCandidateTableName());

    impl.setDescription("test_desc");
    assertEquals("test_desc", impl.getDescription());

    impl.setEnabled(false);
    assertEquals(impl.getEnabled(), false);

    impl.setAlgoAgg(true);
    assertTrue(impl.isAlgoAgg());

    List<Attribute> attribList = new ArrayList<Attribute>();
    UIAggregateImpl impl2 = new UIAggregateImpl("name_01", "desc_01", attribList);

    assertEquals("name_01", impl2.getName());
    assertEquals("name_01", impl2.getCandidateTableName());

    assertEquals("desc_01", impl2.getDescription());

    assertTrue(attribList == impl2.getAttributes());

    UIAggregateImpl impl3 = new UIAggregateImpl("name_02", "desc_02", new ArrayList<Attribute>(),
        new ArrayList<Measure>());

    assertEquals("name_02", impl3.getName());
    assertEquals("name_02", impl3.getCandidateTableName());

    assertEquals("desc_02", impl3.getDescription());

    impl.setEstimateRowCount(1.0);
    assertEquals(1.0, impl.estimateRowCount());

    impl.setEstimateSpace(1.0);
    assertEquals(1.0, impl.estimateSpace());

    OutputStub output = new OutputStub();
    impl.setOutput(output);
    Output out = impl.getOutput();
    assertEquals(output, out);
    
  }
}
