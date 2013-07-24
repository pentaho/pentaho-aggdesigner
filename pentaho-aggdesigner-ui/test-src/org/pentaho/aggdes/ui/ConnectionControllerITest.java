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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaProvider;
import org.pentaho.aggdes.ui.form.controller.ConnectionController;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
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
@ContextConfiguration(locations={"/applicationContext.xml", "/plugins.xml", "/ConnectionControllerITest.xml"})
/**
 * Put tests in here that require some orchestration that is achieve in an isolated unit test
 * on the model(s) or controller(s).  Testing bindings is a good example.
 */
public class ConnectionControllerITest extends JMock {

  public ConnectionControllerITest() throws InitializationError {
    super(ConnectionControllerITest.class);
    try {
    	KettleClientEnvironment.init();
    } catch (Exception e) {
    	e.printStackTrace();
    }
  }

  private ConnectionController controller;

  private JUnit4Mockery context;

  private Document doc;

  private XulDomContainer container;

  private ConnectionModel model;

  private List<MondrianFileSchemaProvider> mondrianFileSchemaProviders;

  private BindingFactory bindingFactory;

  private EventRecorder eventRecorder; //for verifying property change events


  @Autowired
  public void setController(ConnectionController controller) {
    this.controller = controller;
  }
  @Autowired
  public void setModel(ConnectionModel model) {
    this.model = model;
  }
  @Autowired
  public void setSchemaProviderExtensions(List<MondrianFileSchemaProvider> mondrianFileSchemaProviders) {
    this.mondrianFileSchemaProviders = mondrianFileSchemaProviders;
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
    eventRecorder = new EventRecorder();
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

    controller.setXulDomContainer(container);


    //In order to really make this an integration test, there needs to be a BindingFactory that is injected into the controller
    //so we can mock or stub it out and allow the object->object bindings to actually be bound while the xulcomponent bindings
    //are consumed.  Here we are proxying the BindingFactory to achieve this.
    bindingFactory.setDocument(doc);
    //setup the proxy binding factory that will ignore all XUL stuff
    XulSupressingBindingFactoryProxy proxy = new XulSupressingBindingFactoryProxy();
    proxy.setProxiedBindingFactory(bindingFactory);
    controller.setBindingFactory(proxy);

    for(MondrianFileSchemaProvider provider : mondrianFileSchemaProviders) {
      provider.setXulDomContainer(container);
      provider.setBindingFactory(proxy);
    }
  }

  @Test
  public void testApplyEnablementForMondrianFileSchemaProvider() throws Exception {
    final String PROPNAME = "applySchemaSourceEnabled";

    //setup all the bindings
    controller.onLoad();

    eventRecorder.record(model);

    //by default apply should be disabled
    assertFalse(model.isApplySchemaSourceEnabled());

    //text input should enable apply
    mondrianFileSchemaProviders.get(0).setSelected(true);
    mondrianFileSchemaProviders.get(0).setMondrianSchemaFilename("text here should enable apply button");

    assertTrue(model.isApplySchemaSourceEnabled());
    assertEquals(Boolean.TRUE, eventRecorder.getLastValue(PROPNAME));

//  clearing the text should disable it again
    mondrianFileSchemaProviders.get(0).setMondrianSchemaFilename("");

    assertFalse(model.isApplySchemaSourceEnabled());
    assertEquals(Boolean.FALSE, eventRecorder.getLastValue(PROPNAME));
  }

  @Test
  public void testApplyEnablementOnProviderSelection() throws Exception {
    final String PROPNAME = "applySchemaSourceEnabled";

    MondrianFileSchemaProvider prvdr1 = mondrianFileSchemaProviders.get(0);
    MondrianFileSchemaProvider prvdr2 = mondrianFileSchemaProviders.get(1);

    //setup all the bindings
    controller.onLoad();

    eventRecorder.record(model);

    //select 1st provider and enter data
    prvdr1.setMondrianSchemaFilename("abc");
    prvdr1.setSelected(true);

    assertTrue(model.isApplySchemaSourceEnabled());
    assertEquals(Boolean.TRUE, eventRecorder.getLastValue(PROPNAME));

    //select 2nd provider
    eventRecorder.reset();
    prvdr1.setSelected(false);
    prvdr2.setSelected(true);

    assertFalse(model.isApplySchemaSourceEnabled());
    assertEquals(Boolean.FALSE, eventRecorder.getLastValue(PROPNAME));

    //reselect 1st provider
    eventRecorder.reset();
    prvdr2.setSelected(false);
    prvdr1.setSelected(true);

    assertTrue(model.isApplySchemaSourceEnabled());
    assertEquals(Boolean.TRUE, eventRecorder.getLastValue(PROPNAME));
  }

//  @Test
  //This test was never fully implemented.. it's a half-baked approach to testing like a user would
//  public void testFormEnablementForEditModeWhenAnAggIsDefined() throws Exception {
//    //setup all the bindings
//    controller.onLoad();
//
//    eventRecorder.record(model);
//
//    //
//    //Connect to a cube:
//    //
//
//    //setup db connection
//    DatabaseMeta dbMeta = new DatabaseMeta();
//    dbMeta.setName("testDB");
//    model.setDatabaseMeta(dbMeta);
//
//    //select 1st provider and enter schema filename
//    MondrianFileSchemaProvider prvdr1 = mondrianFileSchemaProviders.get(0);
//    prvdr1.setMondrianSchemaFilename("test schema filename");
//    prvdr1.setSelected(true);
//    SchemaProviderUiExtension ext = prvdr1;
//
//    controller.setSchemaProviders(Arrays.asList(ext));
//
//    //apply the schema
//    //do what apply() does without all the XUL code
//    model.setCubeNames(prvdr1.getCubeNames());
//    model.setSelectedSchemaModel(prvdr1.getSchemaModel());
//
//    //select first cube
//    model.setCubeName(model.getCubeNames().get(0));
//
//    //connect
//    controller.connect();
//  }
}

