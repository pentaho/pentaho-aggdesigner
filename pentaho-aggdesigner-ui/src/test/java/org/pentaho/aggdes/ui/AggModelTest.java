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
import java.util.List;
import java.util.Vector;

import org.pentaho.aggdes.ui.form.model.AggModel;
import org.pentaho.aggdes.ui.form.model.DimensionRowModel;
import org.pentaho.aggdes.ui.model.UIAggregate;

import junit.framework.TestCase;

public class AggModelTest extends TestCase {
  public void test() {
    AggModel model = new AggModel();
    assertNull(model.getName());
    assertNull(model.getDesc());
    assertEquals(model.isModified(), false);

    model.setName("New Name");
    assertEquals(model.isModified(), true);

    model.setModified(false);
    model.setDesc("New Desc");
    assertEquals(model.isModified(), true);

    assertEquals("New Name", model.getName());
    assertEquals("New Desc", model.getDesc());

    UIAggregate thinagg = model.getThinAgg();
    assertNotNull(thinagg);

    // clears form, doesn't change thinagg
    model.setThinAgg(null);

    assertEquals(model.isModified(), false);
    assertEquals(thinagg, model.getThinAgg());
    assertEquals(model.getName(), "");
    assertEquals(model.getDesc(), "");

    List<DimensionRowModel> dimensionRowModels = new ArrayList<DimensionRowModel>();
    DimensionRowModel rowModel = new DimensionRowModel();
    dimensionRowModels.add(rowModel);

    model.setDimensionRowModels(dimensionRowModels);

    assertEquals(dimensionRowModels, model.getDimensionRowModels());

    // todo: test syncToAgg()
    
    model.setName( "agg-test" );
    assertEquals( model.getThinAgg().getName(), "" );
    model.synchToAgg();
    assertEquals( model.getThinAgg().getName(), "agg-test" );
  }
}
