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
* Copyright 2006 - 2020 Hitachi Vantara.  All rights reserved.
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
public class HierarchyPkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog(HierarchyPkValidatorTest.class);

  private HierarchyPkValidator bean = new HierarchyPkValidator();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMissingPkOnHierarchy() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));

        // meta expectations
        allowing(meta).getPrimaryKeys(null, null, "store");
        will(returnValue(rsStorePrimaryKeys));

        allowing(rsStorePrimaryKeys).next();
        will(returnValue(false));

        // ignore the other calls
        allowing(meta).getPrimaryKeys(with(any(String.class)), with(any(String.class)), with(any(String.class)));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // asserts
    assertTrue(isMessagePresent(messages, ERROR, "store", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }

  @Test
  public void testOkPkOnHierarchy() throws Exception {
    // expectations
    context.checking(new Expectations() {
      {
        // conn expectations
        one(conn).getMetaData();
        will(returnValue(meta));

        // meta expectations
        allowing(meta).getPrimaryKeys(null, null, "store");
        will(returnValue(rsStorePrimaryKeys));

        allowing(rsStorePrimaryKeys).next();
        will(returnValue(true));

        // ignore the other calls
        allowing(meta).getPrimaryKeys(with(any(String.class)), with(any(String.class)), with(any(String.class)));
      }
    });

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // asserts
    assertTrue(isMessagePresent(messages, OK, "store", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }

}
