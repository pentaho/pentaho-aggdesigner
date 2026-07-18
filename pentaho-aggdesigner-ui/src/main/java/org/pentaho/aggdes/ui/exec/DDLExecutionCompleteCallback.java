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

import org.pentaho.aggdes.ui.model.UIAggregate;

/**
 * Provides a callback mechanism between pentaho-aggdesigner-ui and plugins.
 */
public interface DDLExecutionCompleteCallback {
    public void executionComplete(List<UIAggregate> aggList, Exception e);
}
