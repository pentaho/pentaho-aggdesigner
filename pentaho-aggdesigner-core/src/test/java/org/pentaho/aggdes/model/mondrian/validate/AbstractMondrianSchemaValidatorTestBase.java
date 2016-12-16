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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.model.mondrian.validate;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.Schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.ValidationMessage.Type;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

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
public abstract class AbstractMondrianSchemaValidatorTestBase {

  protected Schema schema;

  protected Connection conn;

  protected Mockery context;

  protected DatabaseMetaData meta;

  protected ResultSet rsSalesFact1997PrimaryKeys;

  protected ResultSet rsSalesFact1997ForeignKey;

  protected ResultSet rsStorePrimaryKeys;

  protected ResultSet rsCount;

  protected Statement stmt;

  protected MondrianSchemaValidator v1;

  protected MondrianSchemaValidator v2;

  protected MondrianSchemaValidator v3;

  protected MondrianSchemaValidator v4;

  protected MondrianSchemaValidator v5;

  protected static final Log logger = LogFactory.getLog(AbstractMondrianSchemaValidatorTestBase.class);

  public void setUp() throws Exception {
    context = new JUnit4Mockery();
    schema = loadSchema("/FoodMart.xml");
    if (null == schema) {
      // end the test
      throw new RuntimeException("unable to load schema from file");
    }
    conn = context.mock(Connection.class);
    meta = context.mock(DatabaseMetaData.class);
    rsSalesFact1997PrimaryKeys = context.mock(ResultSet.class, "rsSalesFact1997PrimaryKeys"); //$NON-NLS-1$
    rsStorePrimaryKeys = context.mock(ResultSet.class, "rsStorePrimaryKeys"); //$NON-NLS-1$
    rsSalesFact1997ForeignKey = context.mock(ResultSet.class, "rsSalesFact1997ForeignKey"); //$NON-NLS-1$
    stmt = context.mock(Statement.class, "stmt");
    rsCount = context.mock(ResultSet.class, "rsCount");
    v1 = context.mock(MondrianSchemaValidator.class, "v1");
    v2 = context.mock(MondrianSchemaValidator.class, "v2");
    v3 = context.mock(MondrianSchemaValidator.class, "v3");
    v4 = context.mock(MondrianSchemaValidator.class, "v4");
    v5 = context.mock(MondrianSchemaValidator.class, "v5");
  }

  protected Schema loadSchema(String classpathRelativePath) {
    try {
      Parser xmlParser = XOMUtil.createDefaultParser();
      if (logger.isDebugEnabled()) {
        logger.debug("creating InputStream from " + classpathRelativePath);
      }
      InputStream is = getClass().getResourceAsStream(classpathRelativePath);
      if (null == is) {
        if (logger.isDebugEnabled()) {
          logger.debug(classpathRelativePath + " not found");
        }
        return null;
      }
      return new Schema(xmlParser.parse(is));
    } catch (XOMException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred; returning null", e);
      }
    }
    return null;
  }

  protected boolean isMessagePresent(List<ValidationMessage> messages, Type type, String... substrings) {
    for (ValidationMessage message : messages) {
      if (message.getType() == type) {
        for (String substring : substrings) {
          if (!message.getMessage().contains(substring)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  protected Cube getCubeByName(String name) {
    for (Cube cube : schema.cubes) {
      if (cube.name.equals(name)) {
        return cube;
      }
    }
    return null;
  }
}
