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
