/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.model.mondrian;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import mondrian.olap.MondrianDef.Cube;
import mondrian.olap.MondrianDef.Schema;

import mondrian.olap.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader.MondrianSchemaLoaderParameter;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

public class ValidationMondrianSchemaLoaderTest extends TestCase {

  private static final Log logger = LogFactory.getLog(ValidationMondrianSchemaLoaderTest.class);

  public static boolean myValidatorCalled;

  public static boolean myValidator2Called;

  private MondrianSchemaLoader bean = new MondrianSchemaLoader();

  private Map<Parameter, Object> parameterValues;

  private File tmpFile;

  @Before
  public void setUp() throws Exception {
    // make a copy of the FoodMart.xml (from the classpath) so that it can be referenced as an absolute file: url
    tmpFile = File.createTempFile("FoodMart", ".xml");
    FileOutputStream out = new FileOutputStream(tmpFile);

    InputStream in = this.getClass().getResourceAsStream("/FoodMart.xml");

    IOUtils.copy(in, out);
    IOUtils.closeQuietly(in);
    IOUtils.closeQuietly(out);

    parameterValues = new HashMap<Parameter, Object>();
    // 2 required parameters
    parameterValues.put(MondrianSchemaLoaderParameter.cube, "Sales");
    parameterValues.put(MondrianSchemaLoaderParameter.connectString,
        "Provider=Mondrian;JdbcDrivers=org.hsqldb.jdbcDriver;Jdbc=jdbc:hsqldb:mem:test;JdbcUser=sa;JdbcPassword=;Catalog=file:"
            + tmpFile.getAbsolutePath());
    ValidationMondrianSchemaLoaderTest.myValidatorCalled = false;
    ValidationMondrianSchemaLoaderTest.myValidator2Called = false;
  }

  @After
  public void tearDown() throws Exception {
    parameterValues = null;
    if (null != tmpFile && tmpFile.exists()) {
      tmpFile.delete();
    }
  }

  @Test
  public void testValidateSchemaNoValidators() {
    // set optional parameter
    parameterValues.put(MondrianSchemaLoaderParameter.validators, null);

    List<ValidationMessage> messages = bean.validateSchema(parameterValues);
    logger.debug("got messages: " + messages);
    assertTrue(messages.isEmpty());
  }

  @Test
  public void testValidateConnectionWithIntegratedSecurity() {

    parameterValues.put(MondrianSchemaLoaderParameter.connectString,
      "Provider=Mondrian;JdbcDrivers=org.hsqldb.jdbcDriver;Jdbc=jdbc:hsqldb:mem:test;"
        + "databaseName=jackrabbit;integratedSecurity=true;JdbcUser=;JdbcPassword=;Catalog=file:"
        + tmpFile.getAbsolutePath());

    String connectionString = bean.getJdbcConnectionString( Util.parseConnectString(
      (String) parameterValues.get( MondrianSchemaLoaderParameter.connectString ) ), true);
    assertTrue(connectionString.contains( "integratedSecurity=true" ));
  }

  @Test
  public void testValidateConnectionWithoutIntegratedSecurity() {

    parameterValues.put(MondrianSchemaLoaderParameter.connectString,
      "Provider=Mondrian;JdbcDrivers=org.hsqldb.jdbcDriver;Jdbc=jdbc:hsqldb:mem:test;"
        + "databaseName=jackrabbit;JdbcUser=user;JdbcPassword=password;Catalog=file:"
        + tmpFile.getAbsolutePath());

    String connectionString = bean.getJdbcConnectionString( Util.parseConnectString(
      (String) parameterValues.get( MondrianSchemaLoaderParameter.connectString ) ), false);
    assertFalse(connectionString.contains( "integratedSecurity=true" ));
  }

  @Test
  public void testValidateSchemaOneValidator() {
    parameterValues.put(MondrianSchemaLoaderParameter.validators,
        "org.pentaho.aggdes.model.mondrian.ValidationMondrianSchemaLoaderTest$MyValidator");

    List<ValidationMessage> messages = bean.validateSchema(parameterValues);
    logger.debug("got messages: " + messages);
    assertTrue(ValidationMondrianSchemaLoaderTest.myValidatorCalled);
  }

  @Test
  public void testValidateSchemaTwoValidators() {
    parameterValues.put(MondrianSchemaLoaderParameter.validators, "org.pentaho.aggdes.model.mondrian.ValidationMondrianSchemaLoaderTest$MyValidator,org.pentaho.aggdes.model.mondrian.ValidationMondrianSchemaLoaderTest$MyValidator2");
    
    List<ValidationMessage> messages = bean.validateSchema(parameterValues);
    logger.debug("got messages: " + messages);
    assertTrue(ValidationMondrianSchemaLoaderTest.myValidatorCalled);
    assertTrue(ValidationMondrianSchemaLoaderTest.myValidator2Called);
  }

  public static class MyValidator implements MondrianSchemaValidator {

    public List<ValidationMessage> validateCube(Schema schema, Cube cube, Connection conn) {
      ValidationMondrianSchemaLoaderTest.myValidatorCalled = true;
      return Collections.emptyList();
    }

  }

  public static class MyValidator2 implements MondrianSchemaValidator {

    public List<ValidationMessage> validateCube(Schema schema, Cube cube, Connection conn) {
      ValidationMondrianSchemaLoaderTest.myValidator2Called = true;
      return Collections.emptyList();
    }

  }
  @Test
  public void testValidationMessage() {
    ValidationMessage ok = new ValidationMessage(ValidationMessage.Type.OK, "ok");
    ValidationMessage error = new ValidationMessage(ValidationMessage.Type.ERROR, "error");
    ValidationMessage warning1 = new ValidationMessage(ValidationMessage.Type.WARNING, "warning");
    ValidationMessage warning2 = new ValidationMessage(ValidationMessage.Type.WARNING, "warning2");
    ValidationMessage warning3 = new ValidationMessage(ValidationMessage.Type.WARNING, "warning");
    
    assertEquals(ok.getMessage(), "ok");
    assertEquals(ok.getType(), ValidationMessage.Type.OK);
    assertEquals(ok.toString(), "ValidationMessage[type=OK,message=\"ok\"]");
    
    // test compareTo
    
    assertEquals(ok.compareTo(error), 1);
    assertEquals(ok.compareTo(warning1), 1);
    assertEquals(warning1.compareTo(error), 1);
    assertEquals(warning1.compareTo(ok), -1);
    
    assertEquals(error.compareTo(ok), -1);
    assertEquals(error.compareTo(warning1), -1);
    
    assertEquals(warning1.compareTo(warning2), -1);
    assertEquals(warning2.compareTo(warning1), 1);
    assertEquals(warning1.compareTo(warning3), 0);
    assertEquals(warning3.compareTo(warning1), 0);
  }

}
