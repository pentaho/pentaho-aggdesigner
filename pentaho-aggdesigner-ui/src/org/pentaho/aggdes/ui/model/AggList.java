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

import java.util.List;

/**
 * List of aggregates in play during a session of the agg designer.
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
