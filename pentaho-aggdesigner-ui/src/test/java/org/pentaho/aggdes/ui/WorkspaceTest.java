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

package org.pentaho.aggdes.ui;

import java.io.File;

import junit.framework.TestCase;

public class WorkspaceTest extends TestCase {
  
  public void testWorkspaceProperties() {
    final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".aggdesigner";
    final String CONFIG_FILE = CONFIG_DIR + File.separator + "aggdesigner.properties";
    final String TMP_CONFIG_FILE = CONFIG_DIR + File.separator + "aggdesigner.properties.backup";
    File file = new File(CONFIG_FILE);
    File tmpFile = new File(TMP_CONFIG_FILE);
    if (file.exists()) {
      file.renameTo(tmpFile);
    }

    // first try to load, file won't be there but properties object will be initialized
    Workspace workspace = new Workspace();
    
    // start off without a config file
    String result = workspace.getWorkspaceProperty("test");
    assertNull(result);
    
    workspace.setWorkspaceProperty("test", "value");
    
    result = workspace.getWorkspaceProperty("test");
    assertEquals(result, "value");
    
    // save the config file
    workspace.storeWorkspaceProperties();
    
    // reload workspace
    workspace = new Workspace();
    
    result = workspace.getWorkspaceProperty("test");
    assertEquals(result, "value");
    
    if (file.exists()) {
      file.delete();
    }
    if (tmpFile.exists()) {
      tmpFile.renameTo(file);
    }
    
  }
}
