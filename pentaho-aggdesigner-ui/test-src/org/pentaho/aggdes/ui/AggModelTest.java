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

package org.pentaho.aggdes.ui;

import java.util.ArrayList;
import java.util.List;

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

  }
}
