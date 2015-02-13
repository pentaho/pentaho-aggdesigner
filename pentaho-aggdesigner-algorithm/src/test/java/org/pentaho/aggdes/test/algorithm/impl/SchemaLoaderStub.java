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

package org.pentaho.aggdes.test.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.SchemaLoader;
import org.pentaho.aggdes.model.ValidationMessage;

/**
 * Stub implementation for <code>SchemaLoader</code>. Provides canned
 * answers and some methods are not implemented.
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
