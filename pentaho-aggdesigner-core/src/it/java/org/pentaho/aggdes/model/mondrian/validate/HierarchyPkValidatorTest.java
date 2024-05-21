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
 * Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.aggdes.model.mondrian.validate;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.model.ValidationMessage;

@RunWith( MockitoJUnitRunner.class )
public class HierarchyPkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog( HierarchyPkValidatorTest.class );

  private HierarchyPkValidator bean = new HierarchyPkValidator();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testMissingPkOnHierarchy() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( meta.getPrimaryKeys( null, null, "store" ) ).thenReturn( rsStorePrimaryKeys );
    when( rsStorePrimaryKeys.next() ).thenReturn( false );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    assertTrue( isMessagePresent( messages, ERROR, "store", "primary key" ) );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
  }

  @Test
  public void testOkPkOnHierarchy() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( meta.getPrimaryKeys( null, null, "store" ) ).thenReturn( rsStorePrimaryKeys );
    when( rsStorePrimaryKeys.next() ).thenReturn( true );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    assertTrue( isMessagePresent( messages, OK, "store", "primary key" ) );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
  }
}
