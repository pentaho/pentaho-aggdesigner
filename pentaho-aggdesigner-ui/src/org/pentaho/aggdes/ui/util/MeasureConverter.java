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

import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Handles the serialization of Measures.
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

