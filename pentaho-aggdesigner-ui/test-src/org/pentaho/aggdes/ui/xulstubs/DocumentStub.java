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

package org.pentaho.aggdes.ui.xulstubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulDomException;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.dom.Attribute;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.dom.Namespace;

public class DocumentStub implements Document {
  static Map<String, XulComponent> elementIdMap = new HashMap<String, XulComponent>();
  public List<Binding> bindings = new ArrayList<Binding>();
  public void addBinding(Binding bind) {
    bindings.add(bind);
  }
  public void addInitializedBinding(Binding b) {
    bindings.add(b);
  }
  public void addOverlay(String src) throws XulException {}
  public XulComponent createElement(String elementName) throws XulException { 
    if (elementName.equals("messagebox")) {
      return new XulMessageBoxStub();
    } else if (elementName.equals("filedialog")) {
      return new XulFileDialogStub();
    } else {
      return null;
    }
  }
  public XulComponent getRootElement() { return null; }
  public boolean isRegistered(String elementName) { return false; }
  public void loadFragment(String id, String src) throws XulException {}
  public void removeOverlay(String src) throws XulException {}
  public void setXulDomContainer(XulDomContainer container) {}
  public void addChild(Element element) {}
  public void addChildAt(Element element, int idx) {}
  public String getAttributeValue(String attributeName) { return null; }
  public List<Attribute> getAttributes() { return null; }
  public List<XulComponent> getChildNodes() { return null; }
  public Document getDocument() { return null; }
  public XulComponent getElementById(String id) {
    return elementIdMap.get(id);
  }
  public XulComponent getElementByXPath(String path) { return null; }
  public Object getElementObject() { return null; }
  public List<XulComponent> getElementsByTagName(String tagName) { return null; }
  public XulComponent getFirstChild() { return null; }
  public String getName() { return null; }
  public Namespace getNamespace() { return null; }
  public XulComponent getParent() { return null; }
  public String getText() { return null; }
  public void removeChild(Element element) {}
  public void replaceChild(XulComponent oldElement, XulComponent newElement) throws XulDomException {}
  public void setAttribute(Attribute attribute) {}
  public void setAttribute(String name, String value) {}
  public void setAttributes(List<Attribute> attribute) {}
  public void setNamespace(String prefix, String uri) {}
  public void invokeLater(Runnable runnable) {
    runnable.run();
  }
	public void loadPerspective(String id) {
	}

}
