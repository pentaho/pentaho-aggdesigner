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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.model.ValidationMessage;

@RunWith( MockitoJUnitRunner.class )
public class CubePkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

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
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( meta.getPrimaryKeys( null, null, "sales_fact_1997" ) ).thenReturn( rsSalesFact1997PrimaryKeys );
    when( rsSalesFact1997PrimaryKeys.next() ).thenReturn( true );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    assertTrue( isMessagePresent( messages, OK, "sales_fact_1997", "primary key" ) );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
  }

  @Test
  public void testOKPKOnFact() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( meta.getPrimaryKeys( null, null, "sales_fact_1997" ) ).thenReturn( rsSalesFact1997PrimaryKeys );
    when( rsSalesFact1997PrimaryKeys.next() ).thenReturn( false );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    assertTrue( isMessagePresent( messages, ERROR, "sales_fact_1997", "primary key" ) );
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
  }
}
