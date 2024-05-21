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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.model.mondrian.validate.MondrianSchemaValidator;

@RunWith( MockitoJUnitRunner.class )
public class MondrianSchemaValidatorManagerTest extends AbstractMondrianSchemaValidatorTestBase {

  private static final Log logger = LogFactory.getLog( MondrianSchemaValidatorManagerTest.class );

  private MondrianSchemaValidatorManager bean = new MondrianSchemaValidatorManager();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testValidateSchema() {
    List<MondrianSchemaValidator> list = new ArrayList<>();
    list.add( v1 );
    list.add( v2 );
    list.add( v3 );
    list.add( v4 );
    list.add( v5 );

    bean.setValidators( list );
    bean.validateCube( schema, getCubeByName( "Sales" ), conn );

    // Verify that validateCube method of each validator is called
    for ( MondrianSchemaValidator validator : list ) {
      verify( validator ).validateCube( eq( schema ), eq( getCubeByName( "Sales" ) ), eq( conn ) );
    }
  }
}
