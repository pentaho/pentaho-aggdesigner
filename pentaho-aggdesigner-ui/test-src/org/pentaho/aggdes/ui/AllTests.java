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

package org.pentaho.aggdes.ui;

import org.pentaho.aggdes.ui.exec.impl.JdbcTemplateSqlExecutorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.pentaho.aggdes.ui");
    //$JUnit-BEGIN$
    suite.addTestSuite(AggregateSummaryModelTest.class);
    suite.addTestSuite(AggListImplTest.class);
    suite.addTestSuite(UIAggregateTest.class);
    suite.addTestSuite(MainControllerTest.class);
    suite.addTestSuite(AggregateNamingServiceTest.class);
    suite.addTestSuite(AggregatePanelTest.class);
    suite.addTestSuite(JdbcTemplateSqlExecutorTest.class);
    suite.addTestSuite(AlgorithmRunnerTest.class);
    suite.addTestSuite(SerializationServiceTest.class);
    suite.addTestSuite(AggControllerTest.class);
    suite.addTestSuite(OutputUIServiceTest.class);
    // incompatible with JUnit 3
    // suite.addTestSuite(XulEventHandlerTest.class);

    //$JUnit-END$
    return suite;
  }

}
