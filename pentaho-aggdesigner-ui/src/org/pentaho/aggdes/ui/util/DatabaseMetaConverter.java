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

import org.pentaho.di.core.database.DatabaseMeta;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DatabaseMetaConverter implements Converter {

  public void marshal(Object arg0, HierarchicalStreamWriter writer, MarshallingContext arg2) {
    DatabaseMeta obj = (DatabaseMeta)arg0;
    writer.setValue(obj.getXML());
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
    try {
      return new DatabaseMeta(reader.getValue());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean canConvert(Class arg0) {
    return (arg0.equals(DatabaseMeta.class)); 
  }

}
