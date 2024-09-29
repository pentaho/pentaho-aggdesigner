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

import java.util.List;

import mondrian.olap.MondrianDef;

import org.pentaho.aggdes.model.ValidationMessage;

/**
 * Validates the database schema using the Mondrian schema.
 * 
 * @author mlowery
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
