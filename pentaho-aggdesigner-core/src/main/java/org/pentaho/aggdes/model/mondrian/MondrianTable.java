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
