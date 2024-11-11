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


package org.pentaho.aggdes.model.mondrian;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Hierarchy;

public class MondrianHierarchy implements Hierarchy {
  String name;
  List<MondrianLevel> levels = new ArrayList<MondrianLevel>();
  
  public MondrianHierarchy(String name) {
      this.name = name;
  }
  
  void addLevel(MondrianLevel level) {
      levels.add(level);
  }
  
  public String getName() {
      return name;
  }
  
  public List<MondrianLevel> getLevels() {
      return levels;
  }
}
