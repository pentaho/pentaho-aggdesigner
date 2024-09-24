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
import org.pentaho.aggdes.algorithm.impl.AlgorithmStub;
import org.pentaho.aggdes.algorithm.impl.SchemaStub;
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
