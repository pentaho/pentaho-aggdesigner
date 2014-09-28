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
