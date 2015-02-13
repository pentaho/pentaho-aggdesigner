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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

@RunWith(JMock.class)
public class MondrianSchemaValidatorManagerTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog(DimensionFkValidatorTest.class);

  private MondrianSchemaValidatorManager bean = new MondrianSchemaValidatorManager();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testValidateSchema() {
    List<MondrianSchemaValidator> list = new ArrayList<MondrianSchemaValidator>();
    list.add(v1);
    list.add(v2);
    list.add(v3);
    list.add(v4);
    list.add(v5);

    context.checking(new Expectations() {
      {
        one(v1).validateCube(with(equal(schema)), with(equal(getCubeByName("Sales"))), with(equal(conn)));
        one(v2).validateCube(with(equal(schema)), with(equal(getCubeByName("Sales"))), with(equal(conn)));
        one(v3).validateCube(with(equal(schema)), with(equal(getCubeByName("Sales"))), with(equal(conn)));
        one(v4).validateCube(with(equal(schema)), with(equal(getCubeByName("Sales"))), with(equal(conn)));
        one(v5).validateCube(with(equal(schema)), with(equal(getCubeByName("Sales"))), with(equal(conn)));
      }
    });

    bean.setValidators(list);
    bean.validateCube(schema, getCubeByName("Sales"), conn);

  }
}
