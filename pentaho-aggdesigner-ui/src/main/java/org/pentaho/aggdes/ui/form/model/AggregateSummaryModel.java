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
