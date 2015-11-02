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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

/**
 *
 */

package org.pentaho.aggdes.ui;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.test.algorithm.impl.AlgorithmStub;
import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.ui.AlgorithmRunner.Callback;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;

/**
 * Tests <code>AlgorithmRunner</code>.
 *
 * @author mlowery
 */
public class AlgorithmRunnerTest extends TestCase {

  private boolean algorithmDoneCalled;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testStart() throws Exception {
    SchemaStub schemaStub = new SchemaStub();
    ConnectionModel connectionModel = new ConnectionModelStub(schemaStub);

    AlgorithmStub algorithmStub = new AlgorithmStub();

    AlgorithmRunner algoRunner = new AlgorithmRunner();

    Map<String, String> algorithmRawParams = new HashMap<String, String>();

    algoRunner.setConnectionModel(connectionModel);
    algoRunner.setAlgorithm(algorithmStub);

    algoRunner.start(algorithmRawParams, new Callback() {
      public void algorithmDone() {
        AlgorithmRunnerTest.this.algorithmDoneCalled = true;
      }
    });


    // TODO: WG: is there a way to refactor this so there is a guarantee vs.
    // a 3 sec wait vs. an 5 second wait?
    Thread.sleep(5000); // wait for algorithm thread to complete...

    assertTrue("Algorithm done not called.", algorithmDoneCalled);
    
    algoRunner.stop();
    assertNotNull( algoRunner.getResult() );
  }
}
