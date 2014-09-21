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

import mondrian.rolap.RolapStar;

import org.pentaho.aggdes.model.Table;

public class MondrianTable implements Table {
  private final MondrianTable parent;
  private final RolapStar.Table table;

  public MondrianTable(MondrianTable parent, RolapStar.Table table) {
      this.parent = parent;
      this.table = table;
  }

  public String getLabel() {
      return table.getTableName();
  }

  public MondrianTable getParent() {
      return parent;
  }

  public RolapStar.Table getStarTable() {
      return table;
  }
}
