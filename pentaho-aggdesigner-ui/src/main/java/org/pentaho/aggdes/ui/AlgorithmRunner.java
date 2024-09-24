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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
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
