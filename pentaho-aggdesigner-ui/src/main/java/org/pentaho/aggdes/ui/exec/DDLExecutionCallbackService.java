/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.aggdes.ui.exec;

import java.util.List;

/**
 * Provides a service to install DDLExecutionCompleteCallback implementations. 
 * These are callback mechanisms between pentaho-aggdesigner-ui and plugins.
 */
public class DDLExecutionCallbackService {
    private List<DDLExecutionCompleteCallback> ddlCallbacks;

    public void setDdlCallbacks(List<DDLExecutionCompleteCallback> ddlCallbacks) {
        this.ddlCallbacks = ddlCallbacks;
    }
    
    public List<DDLExecutionCompleteCallback> getDdlCallbacks() {
        return ddlCallbacks;
    }
}
