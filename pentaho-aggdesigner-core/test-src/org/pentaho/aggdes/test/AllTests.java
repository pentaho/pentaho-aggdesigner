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

package org.pentaho.aggdes.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for org.pentaho.aggdes.test");
    //$JUnit-BEGIN$
    suite.addTestSuite(MondrianSchemaLoaderTest.class);
    suite.addTestSuite(AlgorithmImplTest.class);
    suite.addTestSuite(LatticeImplTest.class);
    suite.addTestSuite(OutputServiceImplTest.class);
    suite.addTestSuite(MonteCarloLatticeImplTest.class);
    suite.addTestSuite(AggDesignerMainTest.class);
    suite.addTestSuite(MondrianSchemaOutputTest.class);
    suite.addTestSuite(TableGeneratorTest.class);
    suite.addTestSuite(SsasToMondrianTest.class);
    suite.addTestSuite(ValidationMondrianSchemaLoaderTest.class);
    //$JUnit-END$
    return suite;
  }

}
