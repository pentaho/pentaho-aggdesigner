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

package org.pentaho.aggdes.ui.model;

import java.util.EventObject;

public class AggListEvent extends EventObject {

  private static final long serialVersionUID = -8654486235997895431L;

  private Type type;

  private int index;
  
  public AggListEvent(AggList src, Type type, int index) {
    super(src);
    this.type = type;
    this.index = index;
  }

  public static enum Type {
    AGG_ADDED, AGGS_ADDED, AGG_REMOVED, AGGS_CLEARED, AGG_CHANGED, SELECTION_CHANGING, SELECTION_CHANGED
  }

  public Type getType() {
    return type;
  }
  
  public int getIndex() {
    return index;
  }

}
