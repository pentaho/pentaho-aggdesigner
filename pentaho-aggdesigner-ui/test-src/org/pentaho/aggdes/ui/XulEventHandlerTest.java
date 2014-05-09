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

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulLoader;
import org.pentaho.ui.xul.dom.Attribute;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "/applicationContext.xml", "/plugins.xml" })
public class XulEventHandlerTest extends AbstractJUnit4SpringContextTests {

	static {
    try {
    	KettleClientEnvironment.init();
    } catch (Exception e) {
    	e.printStackTrace();
    }
	}

  private Map<String, XulEventHandler> handlersByName = new HashMap<String, XulEventHandler>();

  @SuppressWarnings("unchecked")
  @Before
  public void init() {
    Map<?, XulEventHandler> handlerMap =
      applicationContext.getBeansOfType(XulEventHandler.class);
    for (Object handler : handlerMap.values()) {
      handlersByName.put(((XulEventHandler) handler).getName(), (XulEventHandler)handler);
    }
  }

  @Test
  public void reconcileEventHandlers() throws IllegalArgumentException, XulException {
    try {
      XulLoader xulLoader = (XulLoader) applicationContext.getBean("xulLoader");
      XulDomContainer container = xulLoader.loadXul("org/pentaho/aggdes/ui/resources/mainFrame.xul");

      //traverse element tree and do getCommand and test that this method signature exists
      Document xulDoc = container.getDocumentRoot();

      testXulComponentsDeep(xulDoc.getRootElement());
    } catch (java.awt.HeadlessException e) {
      //ignore for now...    	
    }
  }

  private void testXulComponentsDeep(XulComponent comp) {
    for (Attribute attr : comp.getAttributes()) {

      String[] excludes = new String[] { "xmlns", "label", "value", "description", "src", "title", "image", "appicon" };
      boolean excluded = false;

      for (String exclude : excludes) {
        if (attr.getName().matches(".*" + exclude + ".*")) {
          excluded = true;
          break;
        }
      }

      if (!excluded && StringUtils.contains(attr.getValue(), '.')) {
        String[] invokeString = attr.getValue().split("\\.");
        assertTrue("No event handler object exists with name [" + invokeString[0] + "] for this [" + attr.getName()
            + "] event.", handlersByName.containsKey(invokeString[0]));

        boolean methodFound = false;

        XulEventHandler handler = handlersByName.get(invokeString[0]);
        Method[] methods = handler.getClass().getMethods();
        String methodName = invokeString[1].split("\\(")[0];
        for(Method method: methods) {
          if(method.getName().equals(methodName)) {
            methodFound = true;
          }
        }

        assertTrue("No method found by name ["+methodName+"] in event handler ["+handler.getClass()+"]", methodFound);
      }
    }

    for (XulComponent child : comp.getChildNodes()) {
      testXulComponentsDeep(child);
    }

  }
}
