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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulEventSource;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulLoader;
import org.pentaho.ui.xul.XulPerspective;
import org.pentaho.ui.xul.XulSettingsManager;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.DefaultBinding;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;

public class XulDomContainerStub implements XulDomContainer {

  public List<Binding> bindings = new ArrayList<Binding>();
  public DocumentStub document = new DocumentStub();

  public void addBinding(Binding binding) {}
  public void addInitializedBinding(Binding b) {}
  public void addDocument(Document document) {}
  public void addEventHandler(XulEventHandler handler) {}
  public void addEventHandler(String id, String eventClassName) throws XulException {}
  public void close() {}
  public Binding createBinding(XulEventSource source, String sourceAttr, String targetId, String targetAttr) {
    Binding binding = new DefaultBinding(source, sourceAttr, targetId, targetAttr);
    bindings.add(binding);
    return null;
  }
  public Binding createBinding(String source, String sourceAttr, String targetId, String targetAttr) {return null;}
  public Document getDocument(int idx) { return null; }
  public Document getDocumentRoot() { return document; }
  public XulEventHandler getEventHandler(String key) throws XulException { return null; }
  public Map<String, XulEventHandler> getEventHandlers() { return null; }
  public Object getOuterContext() { return null; }
  public XulLoader getXulLoader() { return null; }
  public void initialize() {}
  public Object invoke(String method, Object[] args) throws XulException { return null; }
  public boolean isClosed() { return false; }
  public XulDomContainer loadFragment(String xulLocation) throws XulException { return null; }
  public XulDomContainer loadFragment(String xulLocation, ResourceBundle res) throws XulException { return null; }
  public void loadFragment(String id, String src) throws XulException {}
  public void loadOverlay(String src) throws XulException {}
  public void mergeContainer(XulDomContainer container) {}
  public void registerBinding(XulComponent comp, String expr) {}
  public void removeBinding(Binding binding) {}
  public void removeOverlay(String src) throws XulException {}
  public void setOuterContext(Object context) {}
  public void invokeLater(Runnable runnable) {}
  public boolean isRegistered(String widgetHandlerName) {return true;}
  public XulDomContainer loadFragment(String xulLocation, Object bundle) throws XulException {
    return null;
  }

  public List<Object> getResourceBundles() {
    return null;
  }

  public boolean isInitialized() {
    return false;
  }

  public void setResourceBundles(List<Object> resourceBundles) {

  }
    public void addPerspective(XulPerspective perspective) {
        // TODO Auto-generated method stub

    }
    public void loadPerspective(String id) {
        // TODO Auto-generated method stub
    }

    public void loadOverlay(String src, Object resourceBundle) throws XulException {
        // TODO Auto-generated method stub
    }

    public void registerClassLoader(Object loader) {
        // TODO Auto-generated method stub
    }

    public XulSettingsManager getSettingsManager() {
        // TODO Auto-generated method stub
        return null;
    }
    public void setSettingsManager(XulSettingsManager arg0) {
        // TODO Auto-generated method stub

    }

}
