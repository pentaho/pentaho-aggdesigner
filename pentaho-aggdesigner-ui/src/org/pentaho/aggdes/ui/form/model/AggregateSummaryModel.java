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

package org.pentaho.aggdes.ui.form.model;

import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.stereotype.FormModel;

@FormModel
public class AggregateSummaryModel extends XulEventSourceAdapter {

  String aggregateCount = "";
  String aggregateRows = "";
  String aggregateSpace = "";
  String aggregateLoadTime = "";
  /**
   * returns the number of aggregates that are selected
   * @return
   */
  public String getSelectedAggregateCount() {
    return aggregateCount;
  }
  
  public void setSelectedAggregateCount(String aggregateCount) {
    this.aggregateCount = aggregateCount;
    firePropertyChange("selectedAggregateCount", null, aggregateCount);
  }
  
  public String getSelectedAggregateRows() {
    return aggregateRows;
  }
  
  public void setSelectedAggregateRows(String aggregateRows) {
    this.aggregateRows = aggregateRows;
    firePropertyChange("selectedAggregateRows", null, aggregateRows);
  }
  
  public String getSelectedAggregateSpace() {
    return aggregateSpace;
  }
  
  public void setSelectedAggregateSpace(String aggregateSpace) {
    this.aggregateSpace = aggregateSpace;
    firePropertyChange("selectedAggregateSpace", null, aggregateSpace);
  }
  
  public String getSelectedAggregateLoadTime() {
    return aggregateLoadTime;
  }
  
  public void setSelectedAggregateLoadTime(String aggregateLoadTime) {
    this.aggregateLoadTime = aggregateLoadTime;
    firePropertyChange("selectedAggregateLoadTime", null, aggregateLoadTime);
  }
}
