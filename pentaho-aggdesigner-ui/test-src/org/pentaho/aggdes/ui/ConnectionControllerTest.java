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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.form.controller.ConnectionController;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.aggdes.ui.xulstubs.XulDialogStub;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;

@RunWith(JMock.class)
public class ConnectionControllerTest {

    static {
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

  private XulEventHandler dataHandler;

  private Workspace workspace;

  private OutputService outputService;

  private SchemaProviderUiExtension aSchemaProvider;

  private List<String> cubeNames;

  private SchemaModel providerModel;

  @Before
  public void setUp() throws Exception {
    controller = new ConnectionController();
    context = new JUnit4Mockery();
    doc = context.mock(Document.class);
    container = context.mock(XulDomContainer.class);
    dataHandler = context.mock(XulEventHandler.class);
    model = context.mock(ConnectionModel.class);
    controller.setConnectionModel(model);
    workspace = new Workspace();
    controller.setWorkspace(workspace);
    outputService = context.mock(OutputService.class);
    controller.setOutputService(outputService);
    aSchemaProvider = context.mock(SchemaProviderUiExtension.class);
    cubeNames = Arrays.asList("testCube1", "testCube2");
    providerModel = context.mock(SchemaModel.class);

    // need some expectations here as setXulDomContainer calls getDocumentRoot on the container
    context.checking(new Expectations() {
      {
        one(container).getDocumentRoot();
        will(returnValue(doc));
        allowing(doc).invokeLater(with(any(Runnable.class))); //don't care if the controller uses invokeLater or not
      }
    });

    controller.setXulDomContainer(container);
    controller.setDataHandler(dataHandler);
  }

  private void setupSchemaProviderDefaults() throws AggDesignerException {
    context.checking(new Expectations() {
      {
        allowing(aSchemaProvider).isSelected();
        will(returnValue(true));
        allowing(aSchemaProvider).getCubeNames();
        will(returnValue(cubeNames));
        allowing(aSchemaProvider).getSchemaModel();
        will(returnValue(providerModel));
      }
    });
    controller.setSchemaProviders(Arrays.asList(aSchemaProvider));
  }

  @Test
  public void testReset() throws Exception {
    setupSchemaProviderDefaults();
    context.checking(new Expectations() {
      {
        one(model).reset();
        one(aSchemaProvider).reset();
      }
    });
    controller.reset();
  }

//  @Ignore
//  @Test
//  public void testOnLoad() {
//    fail("Not yet implemented");
//  }

  @Test
  public void testLoadDatabaseDialog() {
    final XulDialog dialog = context.mock(XulDialog.class);
    context.checking(new Expectations() {
      {
        one(doc).getElementById(ConnectionController.GENERAL_DATASOURCE_WINDOW);
        will(returnValue(dialog));
        one(dialog).show();
        allowing(dataHandler).setData(with(any(DatabaseMeta.class)));
        allowing(dataHandler).getData();
        will(returnValue(new DatabaseMeta()));
        allowing(model).getDatabaseMeta();
        will(returnValue(new DatabaseMeta()));
        one(model).setDatabaseMeta(with(any(DatabaseMeta.class)));
      }
    });
    controller.loadDatabaseDialog();
  }

  @Test
  public void testConnect_Success() throws AggDesignerException {
    setupSchemaProviderDefaults();
    controller.setSelectedSchemaProvider(); //mimics the apply having been pressed

    //now call connect
    context.checking(new Expectations() {
      {
        //using dialog stub here instead of a mock so we get thread blocking which is needed to have this test run sucessfully
        final XulDialog waitDialog = new XulDialogStub();
        allowing(doc).getElementById(ConnectionController.ANON_WAIT_DIALOG);
        will(returnValue(waitDialog));

        final XulDialog connDialog = context.mock(XulDialog.class);
        allowing(doc).getElementById(ConnectionController.CONNECTION_DIALOG);
        will(returnValue(connDialog));
        one(connDialog).hide();

        final Schema schema = context.mock(Schema.class);
        one(aSchemaProvider).loadSchema("testCube");
        will(returnValue(schema));

        one(model).getCubeName();
        will(returnValue("testCube"));
        one(model).setSchema(schema);
        ignoring(model).setSchemaUpToDate(with(any(Boolean.class)));

        ignoring(outputService);
      }
    });

    controller.connect();
    //make sure the all the aggdesigner functionality is enabled after a successful connection
    assertTrue(workspace.isApplicationUnlocked());
  }

  @Test
  public void testConnectErrorDialogDismiss_Visible() {
    context.checking(new Expectations() {
      {
        final XulDialog dialog = context.mock(XulDialog.class);
        allowing(doc).getElementById(ConnectionController.CONNECT_ERROR_DIALOG);
        will(returnValue(dialog));
        allowing(dialog).isHidden();
        will(returnValue(false));
        one(dialog).hide();
      }
    });
    controller.connectErrorDialogDismiss();
  }

  @Test
  public void testConnectErrorDialogDismiss_NotVisible() {
    context.checking(new Expectations() {
      {
        final XulDialog dialog = context.mock(XulDialog.class);
        allowing(doc).getElementById(ConnectionController.CONNECT_ERROR_DIALOG);
        will(returnValue(dialog));
        allowing(dialog).isHidden();
        will(returnValue(true));
        never(dialog).hide();
      }
    });
    controller.connectErrorDialogDismiss();
  }

  @Test
  public void testShowConnectionDialog() {
    context.checking(new Expectations() {
      {
        final XulDialog dialog = context.mock(XulDialog.class);
        allowing(doc).getElementById(ConnectionController.CONNECTION_DIALOG);
        will(returnValue(dialog));
        one(dialog).show();
      }
    });

    controller.showConnectionDialog();
  }

  @Test
  public void testHideConnectionDialog() {
    context.checking(new Expectations() {
      {
        final XulDialog dialog = context.mock(XulDialog.class);
        allowing(doc).getElementById(ConnectionController.CONNECTION_DIALOG);
        will(returnValue(dialog));
        one(dialog).hide();
      }
    });

    controller.hideConnectionDialog();
  }

  @Test
  public void testApply_Success() throws XulException, AggDesignerException {
    setupSchemaProviderDefaults();

    context.checking(new Expectations() {
      {
        //using dialog stub here instead of a mock so we get thread blocking which is needed to have this test run sucessfully
        final XulDialog waitDialog = new XulDialogStub();
        allowing(doc).getElementById(ConnectionController.ANON_WAIT_DIALOG);
        will(returnValue(waitDialog));

        one(model).setCubeNames(cubeNames);
        one(model).setSelectedSchemaModel(providerModel);
      }
    });

    controller.apply();
  }
}
