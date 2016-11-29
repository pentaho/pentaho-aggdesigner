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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.ui;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.ui.ext.OutputUiExtension;

public class OutputUIServiceTest extends TestCase {
  
  static class OutputUiExtensionStub implements OutputUiExtension {
    boolean accept = false;
    public boolean accept(Output output) { return accept; }
    public boolean isModified() { return false; }
    public void loadOutput(Output output) {}
    public void saveOutputChanges(Output output) {}
    public String getOverlayPath() { return null; }
    public void onLoad() {}
    public void onUnload() {}
  }
  
  static class OutputStub implements Output {
    public Aggregate getAggregate() { return null; }
  }
  
  public void test() {
    OutputUIService uiService = new OutputUIService();
    OutputStub output = new OutputStub();
    List<OutputUiExtension> extensions = new ArrayList<OutputUiExtension>();
    OutputUiExtensionStub outputUiExtension = new OutputUiExtensionStub();
    extensions.add(outputUiExtension);
    
    // test set
    
    uiService.setOutputUiExtensions(extensions);
    
    OutputUiExtension extension = uiService.getUiExtension(output);
    
    assertNull(extension);
    
    outputUiExtension.accept = true;
    
    extension = uiService.getUiExtension(output);
    
    assertNotNull(extension);
    
    uiService = new OutputUIService();
    
    // test add
    
    uiService.addOutputUiExtension(outputUiExtension);
    
    extension = uiService.getUiExtension(output);
    
    assertNotNull(extension);
    
    outputUiExtension.accept = false;
    
    extension = uiService.getUiExtension(output);
    
    assertNull(extension);
    
  }
}
