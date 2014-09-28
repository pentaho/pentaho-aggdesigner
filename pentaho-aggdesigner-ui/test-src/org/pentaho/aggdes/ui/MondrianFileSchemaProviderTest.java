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
import org.pentaho.di.core.KettleClientEnvironment;
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
        KettleClientEnvironment.init();
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

    assertEquals(Boolean.TRUE, eventRecorder.getLastValue("schemaDefined"));
  }
  @Test
  public void testSchemaDefined_UnDefined() {
    defineSchema();
    eventRecorder.reset();

    undefineSchema();

    assertEquals(Boolean.FALSE, eventRecorder.getLastValue("schemaDefined"));
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
