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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
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
