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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.ui.model.UIAggregate;

public class RuleBasedAggregateNamingServiceImpl implements AggregateNamingService {

  public void nameAggregate(UIAggregate newAggregate, Iterable<UIAggregate> existingAggregates, Schema schema) {
    ArrayList<UIAggregate> newAggregates = new ArrayList<UIAggregate>();
    newAggregates.add(newAggregate);
    nameAggregates(newAggregates, existingAggregates, schema);
  }

  public void nameAggregates(List<UIAggregate> newAggregates, Iterable<UIAggregate> existingAggregates, Schema schema) {
    
    // get the fact table name
    String factName =  ((MondrianSchema)schema).getMeasures().get(0).getTable().getLabel();
    
    String aggNamePre = "agg_";
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
      agg.setName(aggNamePre + (++max) + "_" + factName);
    }
  }
}
