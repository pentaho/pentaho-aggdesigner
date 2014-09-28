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
