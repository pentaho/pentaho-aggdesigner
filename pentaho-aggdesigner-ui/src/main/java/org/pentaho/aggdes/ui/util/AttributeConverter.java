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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
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
