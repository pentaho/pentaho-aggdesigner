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

import static org.pentaho.aggdes.test.util.TestUtils.getTestProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pentaho.aggdes.test.algorithm.impl.SchemaStub;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.controller.ConnectionController;
import org.pentaho.aggdes.ui.form.controller.MainController;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.util.SerializationService;
import org.pentaho.aggdes.ui.xulstubs.XulDomContainerStub;
import org.pentaho.aggdes.ui.xulstubs.XulFileDialogStub;
import org.pentaho.aggdes.ui.xulstubs.XulMessageBoxStub;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;

public class MainControllerTest extends TestCase {

	public void setUp() {
    try {
    	KettleClientEnvironment.init();
    } catch (Exception e) {
    	e.printStackTrace();
    }
    XulMessageBoxStub.openedMessageBoxes.clear();
    XulMessageBoxStub.returnCode = 0;
    XulFileDialogStub.returnFile = null;
  }

  public void testSaveWorkspace() throws Exception {

    // INITIAL STUB WIRING

    Workspace workspace = new Workspace();

    SchemaStub schemaStub = new SchemaStub();

    ConnectionModelImpl connectionModel = new ConnectionModelImpl();
    connectionModel.setDatabaseMeta(new DatabaseMeta());
    MondrianFileSchemaModel schemaModel = new MondrianFileSchemaModel();
    schemaModel.setMondrianSchemaFilename(getTestProperty("test.mondrian.foodmart.connectString.catalog"));
    connectionModel.setSelectedSchemaModel(schemaModel);
    connectionModel.setCubeName("Sales");

    AggList aggList = SerializationServiceTest.getAggList(schemaStub);

    SerializationService serializationService = new SerializationService();
    serializationService.setConnectionModel(connectionModel);
    serializationService.setAggList(aggList);

    workspace.setSchema(schemaStub);
    XulDomContainer xulDomContainer = new XulDomContainerStub();
    MainController controller = new MainController();
    controller.setXulDomContainer(xulDomContainer);
    controller.setWorkspace(workspace);
    controller.setConnectionModel(connectionModel);
    controller.setSerializationService(serializationService);

    // TEST 1 - App Locked

    workspace.setWorkspaceUpToDate(false);
    workspace.setApplicationUnlocked(false);

    controller.saveWorkspace(false);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 1);

    assertTrue(XulMessageBoxStub.openedMessageBoxes.get(0).getMessage().indexOf("Cannot save") >= 0);

    // makes sure we didn't make it past where we were
    assertFalse(workspace.getWorkspaceUpToDate());

    // TEST 2 - User Cancels In File Dialog

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.CANCEL;
    workspace.setApplicationUnlocked(true);

    controller.saveWorkspace(false);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
    assertFalse(workspace.getWorkspaceUpToDate());

    // TEST 3 - Save Design

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.OK;
    XulFileDialogStub.returnFile = new File("temp_design_output.xml");
    if (XulFileDialogStub.returnFile.exists()) {
      XulFileDialogStub.returnFile.delete();
    }

    controller.saveWorkspace(false);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
    assertEquals(workspace.getWorkspaceLocation(), XulFileDialogStub.returnFile);
    assertTrue(workspace.getWorkspaceUpToDate());
    assertTrue(XulFileDialogStub.returnFile.exists());

    // TEST 4 - Save without File Dialog, already has save location

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    if (XulFileDialogStub.returnFile.exists()) {
      XulFileDialogStub.returnFile.delete();
    }

    controller.saveWorkspace(false);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);
    assertEquals(workspace.getWorkspaceLocation(), XulFileDialogStub.returnFile);
    assertTrue(workspace.getWorkspaceUpToDate());
    assertTrue(XulFileDialogStub.returnFile.exists());

    // TEST 5 - Save As

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.OK;
    XulFileDialogStub.returnFile = new File("temp_design_output.xml");
    if (XulFileDialogStub.returnFile.exists()) {
      XulFileDialogStub.returnFile.delete();
    }

    controller.saveWorkspace(true);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
    assertEquals(workspace.getWorkspaceLocation(), XulFileDialogStub.returnFile);
    assertTrue(workspace.getWorkspaceUpToDate());
    assertTrue(XulFileDialogStub.returnFile.exists());

    // TEST 6 - Save to Directory

    workspace.setWorkspaceLocation(null);
    workspace.setWorkspaceUpToDate(false);
    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.OK;
    XulFileDialogStub.returnFile = new File(".");

    controller.saveWorkspace(false);

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 1);
    assertTrue(XulMessageBoxStub.openedMessageBoxes.get(0).getMessage().indexOf("Failed") >= 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
    assertNull(workspace.getWorkspaceLocation());
    assertFalse(workspace.getWorkspaceUpToDate());
  }

  public void testPromptIfSaveRequired() throws XulException {

    // INITIAL STUB WIRING

    Workspace workspace = new Workspace();
    SchemaStub schemaStub = new SchemaStub();

    ConnectionModelImpl connectionModel = new ConnectionModelImpl();

    connectionModel.setDatabaseMeta(new DatabaseMeta());
    MondrianFileSchemaModel schemaModel = new MondrianFileSchemaModel();
    schemaModel.setMondrianSchemaFilename(getTestProperty("test.mondrian.foodmart.connectString.catalog"));
    connectionModel.setSelectedSchemaModel(schemaModel);
    connectionModel.setCubeName("Sales");

    AggList aggList = SerializationServiceTest.getAggList(schemaStub);

    SerializationService serializationService = new SerializationService();
    serializationService.setConnectionModel(connectionModel);
    serializationService.setAggList(aggList);

    workspace.setSchema(schemaStub);
    XulDomContainer xulDomContainer = new XulDomContainerStub();
    MainController controller = new MainController();
    controller.setXulDomContainer(xulDomContainer);
    controller.setWorkspace(workspace);
    controller.setConnectionModel(connectionModel);
    controller.setSerializationService(serializationService);

    // Test 1 - No Prompt Necessary

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    workspace.setApplicationUnlocked(false);
    workspace.setWorkspaceUpToDate(false);

    boolean rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, true);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);

    // Test 2 - Still No Prompt Necessary

    workspace.setApplicationUnlocked(false);
    workspace.setWorkspaceUpToDate(true);

    rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, true);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);

    // Test 3 - Still No Prompt Necessary

    workspace.setApplicationUnlocked(true);
    workspace.setWorkspaceUpToDate(true);

    rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, true);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);

    // Test 4 - Prompt Necessary, Cancel

    workspace.setApplicationUnlocked(true);
    workspace.setWorkspaceUpToDate(false);
    XulMessageBoxStub.returnCode = 2;

    rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, false);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 1);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);

    // Test 4 - Prompt Necessary, Don't Save

    XulMessageBoxStub.openedMessageBoxes.clear();
    workspace.setApplicationUnlocked(true);
    workspace.setWorkspaceUpToDate(false);
    XulMessageBoxStub.returnCode = 1;

    rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, true);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 1);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 0);

    // Test 5 - Prompt Necessary, Save (Cancel)

    XulMessageBoxStub.openedMessageBoxes.clear();
    workspace.setApplicationUnlocked(true);
    workspace.setWorkspaceUpToDate(false);
    XulMessageBoxStub.returnCode = 0;
    XulFileDialogStub.returnCode = RETURN_CODE.OK;

    rtnValue = controller.promptIfSaveRequired();

    assertEquals(rtnValue, false);
    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 2);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
  }

  public void testOpenWorkspace() throws XulException {

    // INITIAL STUB WIRING

    Workspace workspace = new Workspace();
    final SchemaStub schemaStub = new SchemaStub();


    final ConnectionModelImpl connectionModel = new ConnectionModelImpl();

    connectionModel.setDatabaseMeta(new DatabaseMeta());
    MondrianFileSchemaModel schemaModel = new MondrianFileSchemaModel();
    schemaModel.setMondrianSchemaFilename(getTestProperty("test.mondrian.foodmart.connectString.catalog"));
    connectionModel.setSelectedSchemaModel(schemaModel);
    connectionModel.setCubeName("Sales");

    AggList aggList = SerializationServiceTest.getAggList(schemaStub);

    SerializationService serializationService = new SerializationService();
    serializationService.setConnectionModel(connectionModel);
    serializationService.setAggList(aggList);

    workspace.setSchema(schemaStub);
    XulDomContainer xulDomContainer = new XulDomContainerStub();
    MainController controller = new MainController();
    controller.setXulDomContainer(xulDomContainer);
    controller.setWorkspace(workspace);
    controller.setConnectionModel(connectionModel);
    controller.setSerializationService(serializationService);

    final List<Integer> connected = new ArrayList<Integer>();
    final List<Integer> applied = new ArrayList<Integer>();

    ConnectionController connectionController = new ConnectionController() {
      public void connect() {
        connected.add(1);
        connectionModel.setSchema(schemaStub);
      }
      public void apply() {
        applied.add(1);
      }
    };
    connectionController.setConnectionModel(connectionModel);

    controller.setConnectionController(connectionController);

    // Save temporary design

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    workspace.setApplicationUnlocked(true);
    workspace.setWorkspaceUpToDate(true);
    XulFileDialogStub.returnCode = RETURN_CODE.OK;
    XulFileDialogStub.returnFile = new File("temp_design_output.xml");
    if (XulFileDialogStub.returnFile.exists()) {
      XulFileDialogStub.returnFile.delete();
    }

    controller.saveWorkspace(false);

    assertTrue(XulFileDialogStub.returnFile.exists());

    // Test 1 - Cancel Opening

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.CANCEL;

    controller.openWorkspace();

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);

    // Test 2 - Happy Path

    XulMessageBoxStub.openedMessageBoxes.clear();
    XulFileDialogStub.openedFileDialogs.clear();
    XulFileDialogStub.returnCode = RETURN_CODE.OK;
    aggList.clearAggs();
    connected.clear();
    applied.clear();
    controller.openWorkspace();

    assertEquals(XulMessageBoxStub.openedMessageBoxes.size(), 0);
    assertEquals(XulFileDialogStub.openedFileDialogs.size(), 1);
    assertEquals(connected.size(), 1);
    assertEquals(applied.size(), 1);
    assertEquals(aggList.getSize(), 4);
    assertEquals(workspace.isApplicationUnlocked(), true);
    assertEquals(workspace.getWorkspaceUpToDate(), true);

  }

  public void testNewWorkspace() throws Exception {

    Workspace workspace = new Workspace();
    SchemaStub schemaStub = new SchemaStub();

    ConnectionModelImpl connectionModel = new ConnectionModelImpl();
//    ConnectionController connectionController = new ConnectionController();
//    connectionController.setConnectionModel(connectionModel);
//    List<SchemaProviderUiExtension> providerList = new ArrayList<SchemaProviderUiExtension>();
//    MondrianFileSchemaProvider mondrianProvider = new MondrianFileSchemaProvider();
//    providerList.add(mondrianProvider);

    connectionModel.setDatabaseMeta(new DatabaseMeta());
    MondrianFileSchemaModel schemaModel = new MondrianFileSchemaModel();
    schemaModel.setMondrianSchemaFilename(getTestProperty("test.mondrian.foodmart.connectString.catalog"));
    connectionModel.setSelectedSchemaModel(schemaModel);
    connectionModel.setCubeName("Sales");



    AggList aggList = SerializationServiceTest.getAggList(schemaStub);

    SerializationService serializationService = new SerializationService();
    serializationService.setConnectionModel(connectionModel);
    serializationService.setAggList(aggList);

    workspace.setSchema(schemaStub);
    XulDomContainer xulDomContainer = new XulDomContainerStub();
    MainController controller = new MainController();
    final List<Integer> showDialog = new ArrayList<Integer>();
    ConnectionController connectionController = new ConnectionController() {
      public void showConnectionDialog(){
        showDialog.add(1);
      }
    };
    connectionController.setConnectionModel(connectionModel);
    controller.setConnectionController(connectionController);

    controller.setAggList(aggList);
    controller.setXulDomContainer(xulDomContainer);
    controller.setWorkspace(workspace);
    controller.setConnectionModel(connectionModel);
    controller.setConnectionController(connectionController);
    controller.setSerializationService(serializationService);

    controller.newWorkspace();

    assertEquals(aggList.getSize(), 0);
    assertEquals(showDialog.size(), 1);
  }
}
