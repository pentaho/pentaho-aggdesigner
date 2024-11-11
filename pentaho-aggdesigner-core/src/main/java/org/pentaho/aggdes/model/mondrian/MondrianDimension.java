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


package org.pentaho.aggdes.model.mondrian;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Dimension;

public class MondrianDimension implements Dimension {
  String name;
  List<MondrianHierarchy> hierarchies = new ArrayList<MondrianHierarchy>();
  MondrianDimension(String name) {
      this.name = name;
  }
  
  void addHierarchy(MondrianHierarchy hierarchy) {
      hierarchies.add(hierarchy);
  }
  
  public List<MondrianHierarchy> getHierarchies() {
      return hierarchies;
  }
  
  public String getName() {
      return name;
  }
}
