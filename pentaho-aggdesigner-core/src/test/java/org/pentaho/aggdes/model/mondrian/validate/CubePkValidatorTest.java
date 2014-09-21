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

import static org.junit.Assert.assertTrue;
import static org.pentaho.aggdes.model.ValidationMessage.Type.ERROR;
import static org.pentaho.aggdes.model.ValidationMessage.Type.OK;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.model.ValidationMessage;

@RunWith(JMock.class)
public class CubePkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog(CubePkValidatorTest.class);

  CubePkValidator bean = new CubePkValidator();
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
  }
  
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testMissingPKOnFact() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));

        // meta expectations
        allowing(meta).getPrimaryKeys(null, null, "sales_fact_1997");
        will(returnValue(rsSalesFact1997PrimaryKeys));

        one(rsSalesFact1997PrimaryKeys).next();
        will(returnValue(true));

        // ignore the other calls
        allowing(meta).getPrimaryKeys(with(any(String.class)), with(any(String.class)), with(any(String.class)));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // asserts
    assertTrue(isMessagePresent(messages, OK, "sales_fact_1997", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }

  @Test
  public void testOKPKOnFact() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));

        // meta expectations
        allowing(meta).getPrimaryKeys(null, null, "sales_fact_1997");
        will(returnValue(rsSalesFact1997PrimaryKeys));

        one(rsSalesFact1997PrimaryKeys).next();
        will(returnValue(false));

        // ignore the other calls
        allowing(meta).getPrimaryKeys(with(any(String.class)), with(any(String.class)), with(any(String.class)));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // asserts
    assertTrue(isMessagePresent(messages, ERROR, "sales_fact_1997", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }
  




}
