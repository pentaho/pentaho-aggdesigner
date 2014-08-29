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

package org.pentaho.aggdes.ui.exec;

import org.springframework.dao.DataAccessException;

/**
 * Executes DDL and/or DML.
 * 
 * @author mlowery
 */
public interface SqlExecutor {

  /**
   * Executes <code>sql</code> and calls <code>callback</code> after completion.
   * @param sql
   * @param callback
   * @throws DataAccessException
   */
  void execute(String[] sql, ExecutorCallback callback) throws DataAccessException;

  /**
   * Signals to caller of execute that the execution is complete.
   * 
   * @author mlowery
   */
  public static interface ExecutorCallback {
    /**
     * @param e exception that occurred or <code>null</code> if no exception
     */
    void executionComplete(Exception e);
  }
}
