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
