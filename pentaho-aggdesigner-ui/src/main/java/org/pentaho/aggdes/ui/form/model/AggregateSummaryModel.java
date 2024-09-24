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
