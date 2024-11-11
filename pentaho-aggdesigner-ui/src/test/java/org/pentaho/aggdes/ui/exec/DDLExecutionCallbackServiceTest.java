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


package org.pentaho.aggdes.ui.exec;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pentaho.aggdes.ui.model.UIAggregate;

import junit.framework.TestCase;

public class DDLExecutionCallbackServiceTest extends TestCase {

  @Test
  public void test() throws Exception {
    DDLExecutionCallbackService service = new DDLExecutionCallbackService();
    ArrayList<DDLExecutionCompleteCallback> callbacks = new ArrayList<DDLExecutionCompleteCallback>();
    callbacks.add( new ExecutionImpl() );
    callbacks.add( new ExecutionImpl() );
    callbacks.add( new ExecutionImpl() );
    callbacks.add( new ExecutionImpl() );
    service.setDdlCallbacks( callbacks );
    assertTrue( service.getDdlCallbacks() != null && service.getDdlCallbacks().size() == 4 );
  }

  class ExecutionImpl implements DDLExecutionCompleteCallback {
    public void executionComplete( List<UIAggregate> aggList, Exception e ) {
    }
  }
}
