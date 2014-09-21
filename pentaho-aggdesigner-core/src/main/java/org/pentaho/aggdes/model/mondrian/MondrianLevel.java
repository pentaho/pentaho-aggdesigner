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
