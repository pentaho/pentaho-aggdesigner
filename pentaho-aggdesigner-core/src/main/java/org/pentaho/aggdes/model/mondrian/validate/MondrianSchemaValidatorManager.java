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

package org.pentaho.aggdes.model.mondrian.validate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.Schema;

import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

/**
 * Delegates to a list of <code>MondrianSchemaValidator</code>s.
 */
public class MondrianSchemaValidatorManager implements MondrianSchemaValidator {

  List<MondrianSchemaValidator> validators = Collections.emptyList();

  public List<ValidationMessage> validateCube(Schema schema, Cube cube, Connection conn) {
    List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
    for (MondrianSchemaValidator validator : validators) {
      messages.addAll(validator.validateCube(schema, cube, conn));
    }
    return messages;
  }

  public void setValidators(List<MondrianSchemaValidator> validators) {
    this.validators = validators;
  }

}
