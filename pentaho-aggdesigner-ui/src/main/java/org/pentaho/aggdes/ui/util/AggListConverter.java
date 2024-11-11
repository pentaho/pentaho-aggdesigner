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
