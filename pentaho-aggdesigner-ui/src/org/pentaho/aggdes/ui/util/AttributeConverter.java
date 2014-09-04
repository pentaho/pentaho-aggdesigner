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

import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.model.Schema;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class handles the Serialization of Attributes
 * 
 * @author Will Gorman (wgorman@pentaho.com)
 */
public class AttributeConverter implements Converter {

  Schema schema;
  
  public AttributeConverter(Schema schema) {
    this.schema = schema;
  }
  
  public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
    Attribute attribute = (Attribute)object;
    writer.startNode("label");
    writer.setValue(attribute.getLabel());
    writer.endNode();
    writer.startNode("table");
    writer.setValue(attribute.getTable().getLabel());
    writer.endNode();
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    
    reader.moveDown();
    String label = reader.getValue();
    reader.moveUp();
    reader.moveDown();
    String tableLabel = reader.getValue();
    reader.moveUp();
    Attribute foundAttrib = null;
    for (Attribute attribute : schema.getAttributes()) {
      if (attribute.getLabel().equals(label) &&
          attribute.getTable().getLabel().equals(tableLabel)) 
      {
            foundAttrib = attribute;
            break;
      }
    }
    
    if (foundAttrib == null) {
      throw new RuntimeException("Error: Unable to find attribute");
    }
    return foundAttrib;
  }

  public boolean canConvert(Class clazz) {
    return Attribute.class.isAssignableFrom(clazz) && 
           !Measure.class.isAssignableFrom(clazz);
  }

}
