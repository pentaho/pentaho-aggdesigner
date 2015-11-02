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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

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
