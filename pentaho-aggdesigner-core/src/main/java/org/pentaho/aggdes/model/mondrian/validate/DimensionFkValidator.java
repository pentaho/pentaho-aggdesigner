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

import static org.pentaho.aggdes.model.ValidationMessage.Type.OK;
import static org.pentaho.aggdes.model.ValidationMessage.Type.ERROR;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.CubeDimension;
import mondrian.olap.MondrianDef.Schema;
import mondrian.olap.MondrianDef.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.ValidationMessage;

public class DimensionFkValidator extends AbstractMondrianSchemaValidator {

  private static final Log logger = LogFactory.getLog(DimensionFkValidator.class);

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
    
    // ~ Check: Foreign key on dimension table==========================================================================

    String factTableName = ((Table) cube.fact).name;
    String schemaName = ((Table) cube.fact).schema;

    for (CubeDimension dim : cube.dimensions) {
      String foreignKey = dim.foreignKey;
      if (logger.isDebugEnabled()) {
        logger.debug("processing dimension \"" + dim.name + "\"");
      }

      if (foreignKey == null) {
          // we are dealing with a degenerate dimension
          if (logger.isDebugEnabled()) {
              logger.debug("dimension is degenerate, skipping");
          }
          continue;
      }

      if (checkedRelations.containsKey(makeKey(schemaName, factTableName, foreignKey))) {
        if (logger.isDebugEnabled()) {
          logger
              .debug("already checked that foreign key not null on column \"" + (null == schemaName ? "" : schemaName + ".") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                  + factTableName + "." + foreignKey + "\"; skipping"); //$NON-NLS-1$
        }
        continue;
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("checking that foreign key not null on column \"" + (null == schemaName ? "" : schemaName + ".") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
              + factTableName + "." + foreignKey + "\""); //$NON-NLS-1$
        }
      }

      ResultSet rs = null;
      try {
        rs = meta.getColumns(null, schemaName, factTableName, foreignKey);
      } catch (SQLException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e); //$NON-NLS-1$
        }
        return fatal(e, messages);
      }
      boolean isNullable = true;
      try {

        while (rs.next()) {
          if (rs.getString("IS_NULLABLE").equals("NO")) {
            if (logger.isDebugEnabled()) {
              logger.debug("column is not nullable; skipping value check");
            }
            isNullable = false;
          }
          break;
        }
      } catch (SQLException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e); //$NON-NLS-1$
        }
        return fatal(e, messages);
      }
      if (isNullable) {
        if (logger.isDebugEnabled()) {
          logger.debug("falling back on checking column values");
        }

        Statement stmt = null;
        boolean nulls = false;
        try {
          String sql = MessageFormat.format("select count(*) as null_count from {0} where {1} is null",
              (null == schemaName ? "" : schemaName + ".") + factTableName, foreignKey);
          if (logger.isDebugEnabled()) {
            logger.debug("executing query: " + sql);
          }
          stmt = conn.createStatement();
          ResultSet rs2 = stmt.executeQuery(sql);
          while (rs2.next()) {
            long nullCount = rs2.getLong("null_count");
            if (nullCount > 0) {
              if (logger.isDebugEnabled()) {
                logger.debug("foreign key column contains null values");
              }
              nulls = true;
            }
            break;
          }
        } catch (SQLException e) {
          if (logger.isErrorEnabled()) {
            logger.error("an exception occurred", e);
          }
          return fatal(e, messages);
        } finally {
          try {
            if (null != stmt) {
              stmt.close();
            }
          } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
              logger.error("an exception occurred", e);
            }
            return fatal(e, messages);
          }
        }
        if (nulls) {
          append(messages, ERROR, "ERROR_CUBE_FK_CHECK", cube.name, factTableName, foreignKey); //$NON-NLS-1$
        } else {
          append(messages, OK, "OK_CUBE_FK_CHECK", cube.name, factTableName, foreignKey); //$NON-NLS-1$
        }

      } else {
        append(messages, OK, "OK_CUBE_FK_CHECK", cube.name, factTableName, foreignKey); //$NON-NLS-1$
      }
      checkedRelations.put(makeKey(schemaName, factTableName, foreignKey), true);
    }

    return messages;

  }

  /**
   * @param schemaName can be null
   */
  private String makeKey(String schemaName, String tableName, String columnName) {
    return (null == schemaName ? "" : schemaName + ".") + tableName + "." + columnName;
  }

}
