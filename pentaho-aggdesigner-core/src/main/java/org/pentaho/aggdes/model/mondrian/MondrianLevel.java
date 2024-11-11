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

import mondrian.rolap.RolapCubeLevel;

import org.pentaho.aggdes.model.Level;

public class MondrianLevel implements Level {
  String name;
  MondrianAttribute attribute;
  RolapCubeLevel level;
  MondrianLevel parent;
  
  public MondrianLevel(MondrianLevel parent, RolapCubeLevel level, String name, MondrianAttribute attribute) {
      this.parent = parent;
      this.level = level;
      this.name = name;
      this.attribute = attribute;
  }
  
  public RolapCubeLevel getRolapCubeLevel() {
      return level;
  }
  
  public String getName() {
      return name;
  }
  
  public MondrianAttribute getAttribute() {
      return attribute;
  }
  
  public MondrianLevel getParent() {
      return parent;
  }
}
