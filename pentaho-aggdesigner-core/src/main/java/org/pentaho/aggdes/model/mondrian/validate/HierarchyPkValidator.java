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
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Hierarchy;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.MondrianDef.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.ValidationMessage;

public class HierarchyPkValidator extends AbstractMondrianSchemaValidator {

  private static final Log logger = LogFactory.getLog(HierarchyPkValidator.class);

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

    String schemaName = ((Table) cube.fact).schema;

    // ~ Check: Primary key on hierarchies==============================================================================
    for (CubeDimension dim : cube.dimensions) {
      for (Hierarchy hierarchy : dim.getDimension(schema).hierarchies) {
        // if primaryKey then use that; otherwise use key at lowest level
        String primaryKey;
        if (null != hierarchy.primaryKey) {
          primaryKey = hierarchy.primaryKey;
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("skipping primary key check as hierarchy table and fact table are the same");
          }
          // table is cube table--already checked its primary key
          break;
        }

        // if primaryKeyTable then use that; elseif use nested table element; else use fact table
        String primaryKeyTable;
        if (null != hierarchy.primaryKeyTable) {
          primaryKeyTable = hierarchy.primaryKeyTable;
        } else if (null != hierarchy.relation) {
          // TODO mlowery again assuming a table; seems bad
          primaryKeyTable = ((Table) hierarchy.relation).name;
        } else {
          primaryKeyTable = ((Table) cube.fact).name;
        }

        if (checkedRelations.containsKey(primaryKeyTable)) {
          if (logger.isDebugEnabled()) {
            logger
                .debug("already checked that primary key exists on relation \"" + (null == schemaName ? "" : schemaName + ".") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + primaryKeyTable + "\"; skipping"); //$NON-NLS-1$
          }
          continue;
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("checking that primary key exists on relation \"" + (null == schemaName ? "" : schemaName + ".") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + primaryKeyTable + "\""); //$NON-NLS-1$
          }
        }

        ResultSet rs2 = null;
        try {
          rs2 = meta.getPrimaryKeys(null, schemaName, primaryKeyTable);
        } catch (SQLException e) {
          if (logger.isErrorEnabled()) {
            logger.error("an exception occurred", e); //$NON-NLS-1$
          }
          return fatal(e, messages);
        }
        boolean pkHierarchyFound = false;
        try {
          while (rs2.next()) {
            pkHierarchyFound = true;
            break;
          }
        } catch (SQLException e) {
          if (logger.isErrorEnabled()) {
            logger.error("an exception occurred", e); //$NON-NLS-1$
          }
          return fatal(e, messages);
        }
        if (!pkHierarchyFound) {
          append(messages, ERROR, "ERROR_HIERARCHY_TABLE_PK_CHECK", primaryKeyTable); //$NON-NLS-1$
        } else {
          append(messages, OK, "OK_HIERARCHY_TABLE_PK_CHECK", primaryKeyTable); //$NON-NLS-1$
        }
        checkedRelations.put(primaryKeyTable, true);
      }
    }

    return messages;
  }

}
