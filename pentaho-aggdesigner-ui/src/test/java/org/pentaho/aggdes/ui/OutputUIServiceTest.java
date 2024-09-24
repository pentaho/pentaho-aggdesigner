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
