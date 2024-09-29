/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.aggdes.ui.util;

import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class handles the Serialization of Measures
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class MeasureConverter implements Converter {

  Schema schema;
  
  public MeasureConverter(Schema schema) {
    this.schema = schema;
  }
  
  public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
    Measure measure = (Measure)object;
    writer.startNode("label");
    writer.setValue(measure.getLabel());
    writer.endNode();
    writer.startNode("table");
    writer.setValue(measure.getTable().getLabel());
    writer.endNode();
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    
    reader.moveDown();
    String label = reader.getValue();
    reader.moveUp();
    reader.moveDown();
    String tableLabel = reader.getValue();
    reader.moveUp();
    Measure foundMeasure = null;
    for (Measure measure : schema.getMeasures()) {
      if (measure.getLabel().equals(label) &&
          measure.getTable().getLabel().equals(tableLabel)) 
      {
            foundMeasure = measure;
            break;
      }
    }
    
    if (foundMeasure == null) {
      throw new RuntimeException("Error: Unable to find measure");
    }
    return foundMeasure;
  }

  public boolean canConvert(Class clazz) {
    return Measure.class.isAssignableFrom(clazz);
  }

}

