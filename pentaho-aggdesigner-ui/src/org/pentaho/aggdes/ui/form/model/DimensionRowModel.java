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

package org.pentaho.aggdes.ui.form.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dimension;
import org.pentaho.aggdes.model.Hierarchy;
import org.pentaho.aggdes.model.Level;
import org.pentaho.ui.xul.XulEventSourceAdapter;

public class DimensionRowModel extends XulEventSourceAdapter {

  private String dimensionName;

  private Dimension dimension;

  private Vector<String> levelNames;

  private int selectedIndex = 0;
  
  List<Attribute> allLevelAttributes = new ArrayList<Attribute>();
  List<Level> allLevels = new ArrayList<Level>();

  private static final Log logger = LogFactory.getLog(DimensionRowModel.class);

  public void setDimension(Dimension dimension) {
    this.dimension = dimension;
    Vector<String> hierarchyLevelVector = new Vector<String>();

    for (Hierarchy h : dimension.getHierarchies()) {
      List<? extends Level> levels = h.getLevels();
      for (Level level : levels) {
        allLevelAttributes.add(level.getAttribute());
        allLevels.add(level);
        String prefix = (dimension.getHierarchies().size() > 1) ? h.getName() + " : " : "";
        hierarchyLevelVector.add(prefix + level.getName());
      }
    }

    setLevelNames(hierarchyLevelVector);
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
    firePropertyChange("selectedIndex", null, selectedIndex);
  }
  
  public Level getSelectedItem() {
    if ( allLevels.isEmpty()) {
      return null;
    }
    return allLevels.get(selectedIndex);
  }

  public String getDimensionName() {
    return dimension.getName();
  }
  
  public void setDimensionName(String dimensionName) {
    ;
  }

  public Vector<String> getLevelNames() {
    return levelNames;
  }

  public void setLevelNames(Vector<String> levelNames) {
    this.levelNames = levelNames;
  }

  /**
   * See if any of the provided attributes are present in our list of attributes.  If there is
   * a match, make that attribute the selected one.
   * @param attributes List of attributes to set as selected
   */
  public void initSelected(List<Attribute> attributes) {

    for (Attribute attribute : attributes) {
      int index = allLevelAttributes.indexOf(attribute);
      if(index >= 0) {
        setSelectedIndex(index);
        //do not return here, we need the last possible selectable item so we get the leaf of the hierarchy
      }
    }
  }

}
