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

import java.util.List;

import mondrian.olap.MondrianDef;

import org.pentaho.aggdes.model.ValidationMessage;

/**
 * Validates the database schema using the Mondrian schema.
 */
public interface MondrianSchemaValidator {

  /**
   * Validates a cube.
   * @param schema schema containing cube
   * @param cube cube to validate
   * @param conn connection to use
   * @return a list of messages
   */
  List<ValidationMessage> validateCube(MondrianDef.Schema schema, MondrianDef.Cube cube, java.sql.Connection conn);
}
