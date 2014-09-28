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

import static org.pentaho.aggdes.model.ValidationMessage.Type.ERROR;
import static org.pentaho.aggdes.model.ValidationMessage.Type.OK;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.MondrianDef.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.ValidationMessage;

/**
 * Checks primary keys on cubes.
 */
public class CubePkValidator extends AbstractMondrianSchemaValidator {

  private static final Log logger = LogFactory.getLog(CubePkValidator.class);

  public List<ValidationMessage> validateCube(Schema schema, Cube cube, Connection conn) {
    List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

    Map<String, Boolean> checkedRelations = new HashMap<String, Boolean>();

    // ~ Get DatabaseMetaData ==========================================================================================
    DatabaseMetaData meta = null;
    try {
      meta = conn.getMetaData();
    } catch (SQLException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e); //$NON-NLS-1$
      }
      return fatal(e, messages);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("processing cube \"" + cube.name + "\""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // TODO: include validation support for mondrian views
    if (!(cube.fact instanceof Table)) {
        if (logger.isDebugEnabled()) {
            logger.debug("cube \"" + cube.name + "\" contains unsupported fact type, " + cube.fact); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return messages;
    }


    // ~ Check: Primary key on cube's fact table========================================================================


    String relationName = ((Table) cube.fact).name;
    String schemaName = ((Table) cube.fact).schema;

    if (logger.isDebugEnabled()) {
      logger.debug("checking that primary key exists on relation \"" + (null == schemaName ? "" : schemaName + ".") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + relationName + "\""); //$NON-NLS-1$
    }

    ResultSet rs = null;
    try {
      rs = meta.getPrimaryKeys(null, schemaName, relationName);
    } catch (SQLException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e); //$NON-NLS-1$
      }
      return fatal(e, messages);
    }
    boolean pkFound = false;
    try {
      while (rs.next()) {
        pkFound = true;
        break;
      }
    } catch (SQLException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e); //$NON-NLS-1$
      }
      return fatal(e, messages);
    }
    if (!pkFound) {
      append(messages, ERROR, "ERROR_FACT_TABLE_PK_CHECK", cube.name, relationName); //$NON-NLS-1$
    } else {
      append(messages, OK, "OK_FACT_TABLE_PK_CHECK", cube.name, relationName); //$NON-NLS-1$
    }

    return messages;
  }

}
