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

package org.pentaho.aggdes.test;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
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
  public void testValidateSchemaOneValidator() {
    parameterValues.put(MondrianSchemaLoaderParameter.validators,
        "org.pentaho.aggdes.test.ValidationMondrianSchemaLoaderTest$MyValidator");

    List<ValidationMessage> messages = bean.validateSchema(parameterValues);
    logger.debug("got messages: " + messages);
    assertTrue(ValidationMondrianSchemaLoaderTest.myValidatorCalled);
  }

  @Test
  public void testValidateSchemaTwoValidators() {
    parameterValues.put(MondrianSchemaLoaderParameter.validators, "org.pentaho.aggdes.test.ValidationMondrianSchemaLoaderTest$MyValidator,org.pentaho.aggdes.test.ValidationMondrianSchemaLoaderTest$MyValidator2");
    
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
