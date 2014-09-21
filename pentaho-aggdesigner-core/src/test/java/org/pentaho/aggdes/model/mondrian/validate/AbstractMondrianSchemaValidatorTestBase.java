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

/**
 * Base class for schema validator tests.
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
