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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.model.ValidationMessage;

@RunWith(JMock.class)
public class DimensionFkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog(DimensionFkValidatorTest.class);

  private DimensionFkValidator bean = new DimensionFkValidator();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testNullableFalseCheckUsingMetaData() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));


        // meta expectations
        allowing(meta).getColumns(with(aNull(String.class)), with(aNull(String.class)), with(equal("sales_fact_1997")),
            with(any(String.class)));
        will(returnValue(rsSalesFact1997ForeignKey));

        allowing(rsSalesFact1997ForeignKey).next();
        will(returnValue(true));
        allowing(rsSalesFact1997ForeignKey).getString("IS_NULLABLE");
        will(returnValue("NO"));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }

    // asserts
    assertTrue(isMessagePresent(messages, OK, "Sales", "sales_fact_1997", "store_id"));

  }

  @Test
  public void testNullableTrueCheckUsingMetaData() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));
        allowing(conn).createStatement();
        will(returnValue(stmt));

        // stmt expectations
        allowing(stmt).executeQuery(with(any(String.class)));
        will(returnValue(rsCount));
        allowing(stmt).close();

        // rsCount
        allowing(rsCount).next();
        will(returnValue(true));
        allowing(rsCount).getLong("null_count");
        will(returnValue(0L));

        // meta expectations
        allowing(meta).getColumns(with(aNull(String.class)), with(aNull(String.class)), with(equal("sales_fact_1997")),
            with(any(String.class)));
        will(returnValue(rsSalesFact1997ForeignKey));

        allowing(rsSalesFact1997ForeignKey).next();
        will(returnValue(true));
        allowing(rsSalesFact1997ForeignKey).getString("IS_NULLABLE");
        will(returnValue("YES"));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }

    // asserts
    assertTrue(isMessagePresent(messages, OK, "Sales", "sales_fact_1997", "store_id"));

  }

  @Test
  public void testNullableTrueCheckUsingResultSet() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));
        allowing(conn).createStatement();
        will(returnValue(stmt));

        // stmt expectations
        allowing(stmt).executeQuery(with(any(String.class)));
        will(returnValue(rsCount));
        allowing(stmt).close();

        // rsCount
        allowing(rsCount).next();
        will(returnValue(true));
        allowing(rsCount).getLong("null_count");
        will(returnValue(14L));

        // meta expectations
        allowing(meta).getColumns(with(aNull(String.class)), with(aNull(String.class)), with(equal("sales_fact_1997")),
            with(any(String.class)));
        will(returnValue(rsSalesFact1997ForeignKey));

        allowing(rsSalesFact1997ForeignKey).next();
        will(returnValue(true));
        allowing(rsSalesFact1997ForeignKey).getString("IS_NULLABLE");
        will(returnValue("YES"));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }

    // asserts
    assertTrue(isMessagePresent(messages, ERROR, "Sales", "sales_fact_1997", "store_id"));

  }

}
