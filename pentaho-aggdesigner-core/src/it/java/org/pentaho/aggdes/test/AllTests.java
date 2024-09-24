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

package org.pentaho.aggdes.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.pentaho.aggdes.model.mondrian.ValidationMondrianSchemaLoaderTest;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.pentaho.aggdes.test" );
    //$JUnit-BEGIN$
    suite.addTestSuite( MondrianSchemaLoaderTestIT.class );
    suite.addTestSuite( AlgorithmImplTest.class );
    suite.addTestSuite( LatticeImplTest.class );
    suite.addTestSuite( OutputServiceImplTest.class );
    suite.addTestSuite( MonteCarloLatticeImplTest.class );
    suite.addTestSuite( AggDesignerMainTest.class );
    suite.addTestSuite( MondrianSchemaOutputTestIT.class );
    suite.addTestSuite( TableGeneratorTestIT.class );
    suite.addTestSuite( SsasToMondrianTest.class );
    suite.addTestSuite( ValidationMondrianSchemaLoaderTest.class );
    //$JUnit-END$
    return suite;
  }

}
