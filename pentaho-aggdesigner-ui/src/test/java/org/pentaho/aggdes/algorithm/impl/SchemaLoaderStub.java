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

package org.pentaho.aggdes.algorithm.impl;

import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.SchemaLoader;
import org.pentaho.aggdes.model.ValidationMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation for <code>SchemaLoader</code>. Provides canned answers and some methods are not implemented.
 * 
 * @author mlowery
 */
public class SchemaLoaderStub implements SchemaLoader {

  public Schema createSchema(Map<Parameter, Object> parameterValues) {
    return new SchemaStub();
  }

  public List<ValidationMessage> validateSchema(Map<Parameter, Object> parameterValues) {
    return Collections.emptyList();
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public List<Parameter> getParameters() {
    Parameter param = new Parameter() {

      public String getDescription() {
        return "Description";
      }

      public String getName() {
        return "cube";
      }

      public Type getType() {
        return Type.STRING;
      }

      public boolean isRequired() {
        return false;
      }
      
    };
    List<Parameter> list = new ArrayList<Parameter>();
    list.add(param);
    return list;
  }

}
