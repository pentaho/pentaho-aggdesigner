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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
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
