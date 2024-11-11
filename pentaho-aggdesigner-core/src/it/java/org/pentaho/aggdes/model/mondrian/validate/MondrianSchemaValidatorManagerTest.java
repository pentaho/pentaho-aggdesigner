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
