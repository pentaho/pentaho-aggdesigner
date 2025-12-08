/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.ui.form.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.aggdes.ui.util.SerializationService;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulRunner;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulLabel;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

@Controller
public class MainController extends AbstractXulEventHandler {

  public static XulRunner mainFrameInstance;

  private static final Log logger = LogFactory.getLog(MainController.class);

  private Workspace workspace;

  private XulMenuitem paste;

  private XulDeck rightDeck = null;

  private AggListController aggListController;

  private AggList aggList;

  private SerializationService serializationService;

  private ConnectionModel connectionModel;

  private ConnectionController connectionController;

  private BindingFactory bindingFactory;

  private static final String LICENSE_FILE_PATH = "./LICENSE.TXT";

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  public void setAggListController(AggListController aggListController) {
    this.aggListController = aggListController;
  }

  public void setConnectionController(ConnectionController connectionController) {
    this.connectionController = connectionController;
  }

  public void setSerializationService(SerializationService serializationService) {
    this.serializationService = serializationService;
  }

  public void setAggList(AggList aggList) {
    this.aggList = aggList;
  }

  public void onLoad() {

    bindingFactory.setDocument(document);
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);

    bindingFactory.createBinding(workspace, "saveEnabled", "save_button", "!disabled"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bindingFactory.createBinding(workspace, "saveEnabled", "save_menuitem", "!disabled"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bindingFactory.createBinding(workspace, "applicationUnlocked", "save_as_button", "!disabled"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bindingFactory.createBinding(workspace, "applicationUnlocked", "save_as_menuitem", "!disabled"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  }


  public void show(int idx) {
    if (rightDeck == null) {
      rightDeck = (XulDeck) document.getElementById("rightDeck");
    }
    rightDeck.setSelectedIndex(idx);

    if (idx == 2) {
      aggListController.displayNewOrExistingAgg();
    }
  }

  public void openWorkspace() throws XulException {

    if (!promptIfSaveRequired()) {
      return;
    }

    File selectedFile = workspace.getWorkspaceLocation();

    XulFileDialog fc = (XulFileDialog) document.createElement("filedialog");

    RETURN_CODE retVal = fc.showOpenDialog(selectedFile);

    if (retVal == RETURN_CODE.OK) {
      selectedFile = (File) fc.getFile();
      try {
        String xml = FileUtils.readFileToString(selectedFile);
        String connAndAgg[] = serializationService.getConnectionAndAggListElements(xml);

        serializationService.deserializeConnection(connectionModel.getSchema(), connAndAgg[0], connAndAgg[1]);

        // first, verify the file exists
        // make sure this is generic enough for SSAS support

        if (!connectionModel.getSelectedSchemaModel().schemaExists()) {
          XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
          msgBox.setMessage(connectionModel.getSelectedSchemaModel().getSchemaDoesNotExistErrorMessage());
          msgBox.open();
          return;
        }

        // second, verify the checksum

        long checksum = connectionModel.getSelectedSchemaModel().recalculateSchemaChecksum();
        if (checksum != connectionModel.getSelectedSchemaModel().getSchemaChecksum()) {
          // display warning message
          XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
          msgBox.setMessage(Messages.getString("MainController.WarningSchemaChanged"));
          msgBox.open();
        }

        // reset state of app into connected mode
        String cubeName = connectionModel.getCubeName();
        if(StringUtils.isEmpty(cubeName)) {
          throw new IllegalArgumentException("Could not initialize workspace: cubeName is empty");
        }

        // Note: apply sets the cube name to null, so we need to reset it.
        connectionController.apply();

        // resync the cube name
        connectionModel.setCubeName(cubeName);

        // TODO: should connect throw a reasonable error message if db info is invalid?
        connectionController.connect();

        // if application is not unlocked, then we failed to connect to the app.

        if (!workspace.isApplicationUnlocked()) {
          XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
          msgBox.setMessage(Messages.getString("MainController.ErrorFailedToConnect"));
          msgBox.open();
          return;
        }

        // populate aggregates

        serializationService.deserializeAggList(connectionModel.getSchema(), connAndAgg[2]);

        // update state flag

        workspace.setWorkspaceUpToDate(true);

      } catch (AggDesignerException e) {
        XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
        msgBox.setMessage(e.getMessage());
        msgBox.open();
        e.printStackTrace();

      } catch (Exception e) {
        XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
        msgBox.setMessage(Messages.getString("MainController.OpenExceptionMessage"));
        msgBox.open();
        e.printStackTrace();
      }
    }
  }

  public boolean saveWorkspace(boolean saveAs) throws XulException {

    Schema schema = workspace.getSchema();

    // only allow saving if the app is unlocked
    if (!workspace.isApplicationUnlocked()) {
      // display warning
      XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setMessage(Messages.getString("MainController.CannotSaveUntilConnected"));
      msgBox.open();
      return false;
    }

    File selectedFile = workspace.getWorkspaceLocation();
    if (saveAs || selectedFile == null) {
      // display file dialog
      XulFileDialog fc = (XulFileDialog) document.createElement("filedialog");
      // TODO: last browsed in directory?
      RETURN_CODE retVal = fc.showSaveDialog();
      if (retVal == RETURN_CODE.OK) {
        selectedFile = (File) fc.getFile();
      } else {
        return false;
      }
    }
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(selectedFile));
      pw.println(serializationService.serializeWorkspace(schema));
      pw.close();

      workspace.setWorkspaceLocation(selectedFile);
      workspace.setWorkspaceUpToDate(true);

      return true;

    } catch (Exception e) {
      XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setMessage(Messages.getString("MainController.SaveExceptionMessage"));
      msgBox.open();
      e.printStackTrace();
    }

    return false;
  }

  public boolean promptIfSaveRequired() throws XulException {
    // prompt to save if the app is in a non-saved state
    if (workspace.isApplicationUnlocked() && !workspace.getWorkspaceUpToDate()) {
      XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setTitle(Messages.getString("MainController.SaveWorkspaceTitle"));
      msgBox.setMessage(Messages.getString("MainController.SaveWorkspaceMessage"));
      msgBox.setButtons(new String[]{
          Messages.getString("MainController.SaveButton"),
          Messages.getString("MainController.DoNotSaveButton"),
          Messages.getString("MainController.CancelButton")});

      int id = msgBox.open();
      if (id == 2) { // CANCEL
        return false;
      } else if (id == 0) { // SAVE
        // what if they click cancel?
        return saveWorkspace(false);
      }
    }
    return true;
  }

  public void newWorkspace() throws Exception {

    if (!promptIfSaveRequired()) {
      return;
    }

    // clear out connection settings and aggregates
    aggList.clearAggs();
    workspace.setApplicationUnlocked(false);
    connectionController.reset();
    document.invokeLater(new Runnable() {
      public void run() {
        connectionController.showConnectionDialog();
      }
    });

  }

  public boolean onClose() throws XulException {
    return promptIfSaveRequired();
  }

  public void close() throws XulException {
    this.getXulDomContainer().close();
  }

  public void cut() {
    try {
      ((XulWindow) this.getXulDomContainer().getDocumentRoot().getRootElement()).cut();
      paste.setDisabled(false);
    } catch (XulException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void copy() {
    try {
      ((XulWindow) this.getXulDomContainer().getDocumentRoot().getRootElement()).copy();
      paste.setDisabled(false);
    } catch (XulException e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void paste() {
    try {
      ((XulWindow) this.getXulDomContainer().getDocumentRoot().getRootElement()).paste();
      paste.setDisabled(false);
    } catch (XulException e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * Called by XUL event.
   */
  public void validation1Done() {
    final XulDialog validationDialog1 = (XulDialog) document.getElementById("validationDialog1");
    validationDialog1.hide();
  }

  public void validation2Done() {
    final XulDialog validationDialog2 = (XulDialog) document.getElementById("validationDialog2");
    validationDialog2.hide();
  }

  public void helpAboutOpen() {
    XulDialog helpAboutDialog = (XulDialog) document.getElementById("helpAboutDialog");
    helpAboutDialog.show();
  }

  public void helpAboutClose() {
    XulDialog helpAboutDialog = (XulDialog) document.getElementById("helpAboutDialog");
    helpAboutDialog.hide();
  }

  public void helpAboutLoad() {

    String version = "";
    try {
      URL manifestUrl = getClass().getResource("/META-INF/MANIFEST.MF");
      if (manifestUrl != null) {
        Manifest manifest = new Manifest(manifestUrl.openStream());
        Attributes attributes = manifest.getMainAttributes();
        version = attributes.getValue("Implementation-Version");
      } else {
        logger.error("MANIFEST.MF not found.");
      }
    } catch (IOException e) {
      logger.error( "", e);
    }
    XulLabel helpAboutVersionLabel = (XulLabel) document.getElementById("aboutVersion");
    helpAboutVersionLabel.setValue( Messages.getString( "about_version", version ) );

    StringBuilder license = new StringBuilder();
    String line;
    try {
      BufferedReader reader =
        new BufferedReader( new FileReader( LICENSE_FILE_PATH ));
      while ( ( line = reader.readLine() ) != null ) {
        license.append( line + System.getProperty( "line.separator" ) );
      }
    } catch ( Exception ex ) {
      license.append( String.format( "Error reading license file from product directory: \"%s\"", LICENSE_FILE_PATH ) );
      logger.error( "Failed to load the license text", ex );
    }
    XulTextbox helpAboutLicenseTextbox = (XulTextbox) document.getElementById( "aboutLicense" );
    helpAboutLicenseTextbox.setValue( license.toString() );

  }

  public void setWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }

  public void showUserGuide(){
    Runnable runnable = new Runnable() {

      public void run() {
        try {
          edu.stanford.ejalbert.BrowserLauncher launcher = new edu.stanford.ejalbert.BrowserLauncher(null);
          launcher.openURLinBrowser( "https://docs.pentaho.com/pba-aggregation-designer/11.0-aggregation-designer" ); //$NON-NLS-1$\
        } catch (BrowserLaunchingInitializingException ex) {
          logger.error("an exception occurred", ex);
        } catch (UnsupportedOperatingSystemException ex) {
          logger.error("an exception occurred", ex);
        }
      }
    };

    new Thread(runnable).start();

  }

  public ConnectionModel getConnectionModel() {

    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {

    this.connectionModel = connectionModel;
  }
}