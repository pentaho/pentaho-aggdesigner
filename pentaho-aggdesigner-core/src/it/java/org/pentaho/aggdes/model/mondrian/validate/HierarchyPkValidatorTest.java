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
