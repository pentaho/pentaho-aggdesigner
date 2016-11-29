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

