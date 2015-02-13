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
