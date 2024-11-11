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
