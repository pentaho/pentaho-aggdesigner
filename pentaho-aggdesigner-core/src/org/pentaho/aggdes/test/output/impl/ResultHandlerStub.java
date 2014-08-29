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

package org.pentaho.aggdes.test.output.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.ResultHandler;

/**
 * Stub implementation for <code>ResultHandler</code>. Provides canned answers and some methods are not implemented.
 * 
 * @author mlowery
 */
public class ResultHandlerStub implements ResultHandler {

  public void handle(Map<Parameter, Object> parameterValues, Schema schema, Result result) {

  }

  public String getName() {
    return getClass().getSimpleName();
  }

  public List<Parameter> getParameters() {
    return Collections.emptyList();
  }

}
