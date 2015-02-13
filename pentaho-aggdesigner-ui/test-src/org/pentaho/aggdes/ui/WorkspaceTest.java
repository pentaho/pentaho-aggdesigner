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
