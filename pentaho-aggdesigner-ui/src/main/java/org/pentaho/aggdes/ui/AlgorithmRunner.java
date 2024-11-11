/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.ui;

import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils.TextProgress;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;

/**
 * Holder for the command-line arguments. Validates command-line arguments. Starts new thread for running algorithm.
 * Provides callback mechanism to notify UI of done-ness.
 * 
 * @author mlowery
 * 
 */
public class AlgorithmRunner {
  
  private static final Log logger = LogFactory.getLog(AlgorithmRunner.class);

  private static final PrintWriter pw = new PrintWriter(System.out);

  private Result result;
  
  private ConnectionModel connectionModel;

  private Algorithm algorithm;

  public AlgorithmRunner() {
  }

  public void setAlgorithm(Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  public void start(final Map<String, String> algorithmRawParams, final Callback callback) {
    // gen the params
    final Map<Parameter, Object> algorithmParams = ArgumentUtils.validateParameters(algorithm, algorithmRawParams);

    // Run the algorithm.
    final TextProgress progress = new TextProgress(pw);

    new Thread() {
      @Override
      public void run() {
        logger.debug("Calling algorithm run method with parameters: "+algorithmParams);
        result = algorithm.run(connectionModel.getSchema(), algorithmParams, progress);
        callback.algorithmDone();
      }
    }.start();

  }

  public void stop() {
    algorithm.cancel();
  }

  public Result getResult() {
    return result;
  }

  /**
   * Contains the method to call when algorithm is done.
   * 
   * @author mlowery
   */
  public static interface Callback {

    /**
     * Called when algorithm is done either by ending normally or by a call to <code>Algorithm.cancel()</code>.
     */
    void algorithmDone();

  }

  public void setConnectionModel(ConnectionModel connectionModel) {
  
    this.connectionModel = connectionModel;
  }

}
