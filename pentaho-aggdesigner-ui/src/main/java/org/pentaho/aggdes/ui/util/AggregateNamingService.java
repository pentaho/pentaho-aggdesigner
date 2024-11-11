/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
