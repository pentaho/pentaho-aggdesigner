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
 * 
 * @author mlowery
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
