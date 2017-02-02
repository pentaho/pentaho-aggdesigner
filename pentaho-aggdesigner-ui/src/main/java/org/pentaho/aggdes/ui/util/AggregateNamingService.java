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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
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
