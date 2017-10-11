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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
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
