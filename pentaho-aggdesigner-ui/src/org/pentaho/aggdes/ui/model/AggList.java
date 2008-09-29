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
