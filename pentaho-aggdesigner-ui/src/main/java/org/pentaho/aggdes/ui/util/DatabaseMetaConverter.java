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
