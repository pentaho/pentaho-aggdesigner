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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
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
    suite.addTestSuite(AggregateNamingServiceIT.class);
    suite.addTestSuite(AggregatePanelIT.class);
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
