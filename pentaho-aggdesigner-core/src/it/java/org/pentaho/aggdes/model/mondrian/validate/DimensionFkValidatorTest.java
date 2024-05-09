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
public class DimensionFkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog( DimensionFkValidatorTest.class );

  private DimensionFkValidator bean = new DimensionFkValidator();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testNullableFalseCheckUsingMetaData() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( meta.getColumns( null, null, "sales_fact_1997", null ) ).thenReturn( rsSalesFact1997ForeignKey );
    when( rsSalesFact1997ForeignKey.next() ).thenReturn( true );
    when( rsSalesFact1997ForeignKey.getString( "IS_NULLABLE" ) ).thenReturn( "NO" );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
    assertTrue( isMessagePresent( messages, OK, "Sales", "sales_fact_1997", "store_id" ) );
  }

  @Test
  public void testNullableTrueCheckUsingMetaData() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( conn.createStatement() ).thenReturn( stmt );
    when( stmt.executeQuery( "SELECT COUNT(*) AS null_count FROM sales_fact_1997 WHERE store_id IS NULL" ) )
      .thenReturn( rsCount );
    when( rsCount.next() ).thenReturn( true );
    when( rsCount.getLong( "null_count" ) ).thenReturn( 0L );
    when( meta.getColumns( null, null, "sales_fact_1997", null ) ).thenReturn( rsSalesFact1997ForeignKey );
    when( rsSalesFact1997ForeignKey.next() ).thenReturn( true );
    when( rsSalesFact1997ForeignKey.getString( "IS_NULLABLE" ) ).thenReturn( "YES" );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
    assertTrue( isMessagePresent( messages, OK, "Sales", "sales_fact_1997", "store_id" ) );
  }

  @Test
  public void testNullableTrueCheckUsingResultSet() throws Exception {
    // Mocking expectations
    when( conn.getMetaData() ).thenReturn( meta );
    when( conn.createStatement() ).thenReturn( stmt );
    when( stmt.executeQuery( "SELECT COUNT(*) AS null_count FROM sales_fact_1997 WHERE store_id IS NULL" ) )
      .thenReturn( rsCount );
    when( rsCount.next() ).thenReturn( true );
    when( rsCount.getLong( "null_count" ) ).thenReturn( 14L );
    when( meta.getColumns( null, null, "sales_fact_1997", null ) ).thenReturn( rsSalesFact1997ForeignKey );
    when( rsSalesFact1997ForeignKey.next() ).thenReturn( true );
    when( rsSalesFact1997ForeignKey.getString( "IS_NULLABLE" ) ).thenReturn( "YES" );

    List<ValidationMessage> messages = bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Assertions
    if ( logger.isDebugEnabled() ) {
      logger.debug( "got " + messages.size() + " message(s): " + messages );
    }
    assertTrue( isMessagePresent( messages, ERROR, "Sales", "sales_fact_1997", "store_id" ) );
  }
}
