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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.Main;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.ui.model.UIAggregate;

public class AggregateNamingServiceImpl implements AggregateNamingService {

  public void nameAggregate(UIAggregate newAggregate, Iterable<UIAggregate> existingAggregates, Schema schema) {
    ArrayList<UIAggregate> newAggregates = new ArrayList<UIAggregate>();
    newAggregates.add(newAggregate);
    nameAggregates(newAggregates, existingAggregates, schema);
  }

  public void nameAggregates(List<UIAggregate> newAggregates, Iterable<UIAggregate> existingAggregates, Schema schema) {
    
    // get schema name, cleanse it to 10 chars and no special chars
    String schemaName = ((MondrianSchema)schema).getRolapConnection().getSchema().getName();
    schemaName = Main.depunctify(schemaName);
    if (schemaName.length() > 10) {
      schemaName = schemaName.substring(0, 10);
    }

    // get cube name, cleanse it to 10 chars and no special chars
    String cubeName = ((MondrianSchema)schema).getRolapCube().getName();
    cubeName = Main.depunctify(cubeName);
    if (cubeName.length() > 10) {
      cubeName = cubeName.substring(0, 10);
    }
    
    String aggNamePre = schemaName + "_" + cubeName + "_";
    int max = 0;

    // determine maximum agg count
    if (existingAggregates != null) {
      for (UIAggregate agg : existingAggregates) {
        if (agg.getName().startsWith(aggNamePre)) {
          String val = agg.getName().substring(aggNamePre.length());
          try {
            int curr = Integer.parseInt(val);
            if (curr > max) {
              max = curr;
            }
          } catch (NumberFormatException e) {
            // ignore any renaming of the aggs
          }
        }
      }
    }
    
    // rename all aggs
    for (UIAggregate agg : newAggregates) {
      agg.setName(aggNamePre + (++max));
    }
  }
}
