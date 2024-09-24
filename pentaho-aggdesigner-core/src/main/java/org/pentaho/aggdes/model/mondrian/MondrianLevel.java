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
