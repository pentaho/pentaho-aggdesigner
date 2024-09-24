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

import java.util.List;

/**
 * List of aggregates in play during a session of the agg designer. 
 * 
 * @author mlowery
 */
public interface AggList extends Iterable<UIAggregate> {

  void addAggListListener(AggListListener l);

  void removeAggListListener(AggListListener l);

  UIAggregate getAgg(int index);

  void removeAgg(int index);

  void setSelectedIndex(int index);
  
  int getSelectedIndex();
  
  UIAggregate getSelectedValue();
  
  int getSize();

  void addAgg(UIAggregate agg);
  
  void aggChanged(UIAggregate agg);
  
  void addAggs(List<UIAggregate> aggs);
  
  void clearAggs();
  
  void moveAggUp(UIAggregate agg);
  
  void moveAggDown(UIAggregate agg);
  
  void checkAll();
  
  void uncheckAll();
  
}
