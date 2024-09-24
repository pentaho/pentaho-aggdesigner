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

package org.pentaho.aggdes.ui;

import org.pentaho.aggdes.ui.exec.impl.JdbcTemplateSqlExecutorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite( "Test for org.pentaho.aggdes.ui" );
    //$JUnit-BEGIN$
    suite.addTestSuite( AggregateSummaryModelTest.class );
    suite.addTestSuite( AggListImplTest.class );
    suite.addTestSuite( UIAggregateTest.class );
    suite.addTestSuite( MainControllerTest.class );
    suite.addTestSuite( JdbcTemplateSqlExecutorTest.class );
    suite.addTestSuite( AlgorithmRunnerTest.class );
    suite.addTestSuite( SerializationServiceTest.class );
    suite.addTestSuite( AggControllerTest.class );
    suite.addTestSuite( OutputUIServiceTest.class );
    // incompatible with JUnit 3
    // suite.addTestSuite(XulEventHandlerTest.class);

    //$JUnit-END$
    return suite;
  }

}
