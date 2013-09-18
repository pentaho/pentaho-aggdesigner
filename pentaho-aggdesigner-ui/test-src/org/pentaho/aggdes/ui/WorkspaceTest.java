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
