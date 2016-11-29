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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.AggListImpl;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class handles the Serialization of AggList
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class AggListConverter implements Converter {

  private AggList aggList;
  public AggListConverter(AggList aggList){
    this.aggList = aggList;
  }
  
  public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext context) {
    AggList impl = (AggList)arg0;
    for (int i = 0; i < impl.getSize(); i++) {
      writer.startNode("aggregation");
      context.convertAnother(impl.getAgg(i));
      writer.endNode();
    }
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    List<UIAggregate> list = new ArrayList<UIAggregate>();
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      if ("aggregation".equals(reader.getNodeName())) {
              UIAggregateImpl agg = (UIAggregateImpl)context.convertAnother(null, UIAggregateImpl.class);
              list.add(agg);
      }
      reader.moveUp();
    }
    aggList.clearAggs();
    aggList.addAggs(list);
    return aggList; 
  }

  public boolean canConvert(Class clazz) {
    return clazz.equals(AggListImpl.class);
  }

}
