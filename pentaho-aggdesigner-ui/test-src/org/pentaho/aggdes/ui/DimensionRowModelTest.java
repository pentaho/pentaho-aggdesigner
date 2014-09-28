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
import java.util.Vector;

import junit.framework.TestCase;

import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.ui.form.model.DimensionRowModel;

public class DimensionRowModelTest extends TestCase {

  public void test() {
    DimensionRowModel rowModel = new DimensionRowModel();
    SchemaStub schema = new SchemaStub();
    rowModel.setDimension(schema.getDimensions().get(0));

    assertEquals(rowModel.getSelectedIndex(), 0);

    rowModel.setSelectedIndex(1);

    assertEquals(rowModel.getSelectedIndex(), 1);

    assertEquals(rowModel.getSelectedItem(), schema.getDimensions().get(0).getHierarchies().get(0).getLevels().get(1));

    assertEquals(rowModel.getDimensionName(), "Dimension 1");

    // TODO: phase out this method!
    rowModel.setDimensionName("this does nothing");

    assertEquals(rowModel.getDimensionName(), "Dimension 1");

    List<Attribute> attributes = new ArrayList<Attribute>();
    attributes.add(schema.getDimensions().get(0).getHierarchies().get(0).getLevels().get(0).getAttribute());

    rowModel.initSelected(attributes);

    assertEquals(rowModel.getSelectedIndex(), 0);

    assertEquals(rowModel.getLevelNames().size(), 2 );
    assertEquals(rowModel.getLevelNames().get(0), "Level 1");



    Vector<String> levelNames = new Vector<String>();
    levelNames.add("test");

    // TODO: phase out this set method, this should be private.
    rowModel.setLevelNames(levelNames);
    assertEquals(rowModel.getLevelNames(), levelNames);

  }

}
