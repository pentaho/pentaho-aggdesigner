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
 * Copyright 2008 Pentaho Corporation.  All rights reserved. 
*/
package org.pentaho.aggdes.ui;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaProvider;
import org.pentaho.aggdes.ui.xulstubs.XulSupressingBindingFactoryProxy;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml", "/plugins.xml"})
public class MondrianFileSchemaProviderTest extends JMock {

  public MondrianFileSchemaProviderTest() throws InitializationError {
    super(MondrianFileSchemaProviderTest.class);
    try {
    	KettleEnvironment.init(false);
    } catch (Exception e) {
    	e.printStackTrace();
    }
  }

  private MondrianFileSchemaProvider schemaProvider;

  private JUnit4Mockery context;

  private Document doc;

  private XulDomContainer container;
  
  private BindingFactory bindingFactory;
  
  private EventRecorder eventRecorder;
  
  @Autowired
  public void setSchemaProvider(MondrianFileSchemaProvider schemaProvider) {
    this.schemaProvider = schemaProvider;
  }

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  @Before
  public void setUp() throws Exception {
    /*
     * In this integration test, we want to mock only the XUL framework, all other components
     * we want to be the "real" ones.  This will allow us to test application behavior without
     * dependency on a UI.
     */
    context = new JUnit4Mockery();
    doc = context.mock(Document.class);
    container = context.mock(XulDomContainer.class);

    // need some expectations here as setXulDomContainer calls getDocumentRoot on the container
    context.checking(new Expectations() {
      {
        allowing(container).getDocumentRoot();
        will(returnValue(doc));
        allowing(container).addEventHandler(with(any(XulEventHandler.class)));
        allowing(doc).addOverlay(with(any(String.class)));
        ignoring(container);
        allowing(doc).getElementById(with(aNonNull(String.class))); 
        will(returnValue(context.mock(XulComponent.class, Long.toString(System.currentTimeMillis()))));
        allowing(doc).addInitializedBinding(with(any(Binding.class)));
        allowing(doc).invokeLater(with(any(Runnable.class))); //don't care if the controller uses invokeLater or not, this is UI stuff
      }
    });
    
    schemaProvider.setXulDomContainer(container);
    
    
    //In order to really make this an integration test, there needs to be a BindingFactory that is injected into the controller
    //so we can mock or stub it out and allow the object->object bindings to actually be bound while the xulcomponent bindings
    //are consumed.  Here we are proxying the BindingFactory to acheive this.
    bindingFactory.setDocument(doc);
    //setup the proxy binding factory that will ignore all XUL stuff
    XulSupressingBindingFactoryProxy proxy = new XulSupressingBindingFactoryProxy();
    proxy.setProxiedBindingFactory(bindingFactory);
    schemaProvider.setBindingFactory(proxy);
    
    schemaProvider.onLoad();
    
    eventRecorder = new EventRecorder();
    eventRecorder.setLogging(true);
    eventRecorder.record(schemaProvider);
  }
  
  @Test
  public void testSchemaDefined_DefaultState() {
    schemaProvider.setSelected(true);
    
    assertEquals(getDefaultDefinedState(), schemaProvider.isSchemaDefined());
  }
  @Test
  public void testSchemaDefined_Defined() {
    undefineSchema();
    eventRecorder.reset();
    
    defineSchema();
    
    assertEquals(Boolean.TRUE, (Boolean)eventRecorder.getLastValue("schemaDefined"));
  }
  @Test
  public void testSchemaDefined_UnDefined() {
    defineSchema();
    eventRecorder.reset();
    
    undefineSchema();
    
    assertEquals(Boolean.FALSE, (Boolean)eventRecorder.getLastValue("schemaDefined"));
  }
  
  
  /**
   * change the state of your schema provider so it is considered to have a defined schema
   */
  private void defineSchema() {
    schemaProvider.setMondrianSchemaFilename("abc");
  }
  private void undefineSchema() {
    schemaProvider.setMondrianSchemaFilename("");
  }
  private boolean getDefaultDefinedState() {
    return (schemaProvider.getMondrianSchemaFilename() == null)?false:schemaProvider.getMondrianSchemaFilename().length() > 0;
  }

}
