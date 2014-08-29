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

package org.pentaho.aggdes.ui.util;

import java.util.List;

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.model.UIAggregate;

/**
 * This interface defines an Aggregate Naming Service, which
 * renames algorithm and new aggregates when they are created
 * to a standard naming convention.
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public interface AggregateNamingService {

  /**
   * rename an aggregate to a preferred name, with access to all existing
   * aggregates and the schema
   * @param newAggregate new aggregate to rename
   * @param existingAggregates existing aggregates
   * @param schema current schema
   */
  public void nameAggregate(UIAggregate newAggregate, Iterable<UIAggregate> existingAggregates, Schema schema);
  
  /**
   * rename a list of aggregates to a preferred name, with access to all existing
   * 
   * @param newAggregates list of new aggregates to rename
   * @param existingAggregates existing aggregates
   * @param schema current schema
   */
  public void nameAggregates(List<UIAggregate> newAggregates, Iterable<UIAggregate> existingAggregates, Schema schema);

}
