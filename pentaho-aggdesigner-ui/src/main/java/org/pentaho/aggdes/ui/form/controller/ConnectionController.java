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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.ui.form.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulEventSource;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.impl.XulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.pentaho.ui.xul.stereotype.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Controller
public class ConnectionController extends AbstractXulEventHandler {

  public static final String CONNECT_ERROR_DIALOG = "connectErrorDialog";

  public static final String CONNECTION_DIALOG = "connection_dialog";

  public static final String ANON_WAIT_DIALOG = "anonWaitDialog";

  public static final String GENERAL_DATASOURCE_WINDOW = "general-datasource-window";

  private static final Log logger = LogFactory.getLog(ConnectionController.class);

  private ConnectionModel connectionModel;

  private XulEventHandler dataHandler;

  private Workspace workspace;

  private OutputService outputService;

  private List<SchemaProviderUiExtension> schemaProviders = new ArrayList<SchemaProviderUiExtension>();

  private SchemaProviderUiExtension selectedSchemaProvider = null;

  private Schema schema = null;

  public BindingFactory bindingFactory;

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  public void setOutputService(OutputService outputService) {
    this.outputService = outputService;
  }

  public List<SchemaProviderUiExtension> getSchemaProviders() {
    return schemaProviders;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {

    this.connectionModel = connectionModel;
  }

  public void setDataHandler(XulEventHandler dataHandler) {

    this.dataHandler = dataHandler;
  }

  public void setWorkspace(Workspace workspace) {

    this.workspace = workspace;
  }

  @Autowired
  public void setSchemaProviders(List<SchemaProviderUiExtension> schemaProviders) {
    this.schemaProviders = schemaProviders;
  }

  public void setSelectedSchemaProvider() {
    selectedSchemaProvider = null;
    for (SchemaProviderUiExtension sProvider : schemaProviders) {
      if (sProvider.isSelected()) {
        selectedSchemaProvider = sProvider;
      }
    }
  }

  public void reset() throws Exception {
    connectionModel.reset();
    for (SchemaProviderUiExtension extension : schemaProviders) {
      extension.reset();
    }
  }

  @RequestHandler
  public void onLoad() throws Exception {
    BindingFactory bf = bindingFactory;
    bf.setDocument(document);

    for (final SchemaProviderUiExtension extension : schemaProviders) {
      try {
        document.addOverlay(extension.getOverlayPath());
        getXulDomContainer().addEventHandler(extension);
        
        ((XulEventSource) extension).addPropertyChangeListener(new PropertyChangeListener() {

          public void propertyChange(PropertyChangeEvent evt) {
            boolean schemaAppliable = false;
            if (evt.getPropertyName().equals("schemaDefined") || evt.getPropertyName().equals("selected")) {
              logger.debug("*** got schemaDefined=" + evt.getNewValue()
                  + ", checking if any providers are applyable");
              for (SchemaProviderUiExtension ex : schemaProviders) {

                //De-select other extensions
                if(evt.getPropertyName().equals("selected") && extension != ex && evt.getNewValue() == Boolean.TRUE){
                  ex.setSelected(false);
                }
                
                if (ex.isSchemaDefined() && ex.isSelected()) {
                  logger.debug("provider " + ex.getName() + " is applyable");
                  schemaAppliable = true;
                } else {
                  logger.debug(ex.getName() + " NOT applyable: defined=[" + ex.isSchemaDefined() + "] selected=["
                      + ex.isSelected() + "]");
                }
              }
              connectionModel.setApplySchemaSourceEnabled(schemaAppliable);
            }
          }

        });
        
        bf.setBindingType(Binding.Type.ONE_WAY);
        bf.createBinding(connectionModel, "schemaLocked", extension, "!enabled");
        
//        extension.onLoad();
      } catch (XulException e) {
        logger.error("Error loading Schema Provider Overlay", e);
      }
    }

    bf.setBindingType(Binding.Type.BI_DIRECTIONAL);

    bf.createBinding(workspace, "applicationUnlocked", "open_advisor", "!disabled");
    bf.createBinding(workspace, "applicationUnlocked", "open_export", "!disabled");
    bf.createBinding(workspace, "applicationUnlocked", "agg_add_btn", "!disabled");
    bf.createBinding(connectionModel, "cubeNames", "cubeSelector", "elements");
    bf.createBinding(connectionModel, "cubeName", "cubeSelector", "selectedItem");

    bf.setBindingType(Binding.Type.ONE_WAY);

    bf.createBinding(connectionModel, "applySchemaSourceEnabled", "applySchemaSourceButton", "!disabled");
    bf.createBinding(connectionModel, "databaseName", "databaseName", "value");
    bf.createBinding(connectionModel, "cubeSelectionEnabled", "cubeSelector", "!disabled");

    bf.createBinding(connectionModel, "schemaLocked", "cubeSelector", "disabled");
    bf.createBinding(connectionModel, "schemaLocked", "applySchemaSourceButton", "disabled");
    
    bf.createBinding(connectionModel, "connectEnabled", "connection_dialog_accept", "!disabled").fireSourceChanged();

    connectionModel.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selectedSchemaModel")) {
          SchemaModel model = (SchemaModel) evt.getNewValue();
          // select the correct schema value
          for (SchemaProviderUiExtension extension : schemaProviders) {
            if (model != null && extension.supportsSchemaModel(model)) {
              selectedSchemaProvider = extension;
              extension.setSelected(true);
              extension.setSchemaModel(model);
            } else {
              extension.setSelected(false);
            }
          }
        }
      }
    });
    
    //call unload after the bindings since the extension's onload might trigger something
    for(SchemaProviderUiExtension extension : schemaProviders) {
      extension.onLoad();
    }
    
    // auto select provider if only one is present
    if (schemaProviders.size() == 1) {
      schemaProviders.get(0).setSelected(true);
    }

    document.invokeLater(new Runnable() {
      public void run() {
        showConnectionDialog();
      }
    });

  }

  @RequestHandler
  public void loadDatabaseDialog() {
    dataHandler.setData(connectionModel.getDatabaseMeta());

    XulDialog dialog = (XulDialog) document.getElementById(GENERAL_DATASOURCE_WINDOW);
    dialog.show();

    DatabaseMeta databaseMeta = (DatabaseMeta) dataHandler.getData();

    if (databaseMeta != null) {
      connectionModel.setDatabaseMeta(databaseMeta);
    }
  }

  //  TODO: What if cancel is pressed?
  @RequestHandler
  public void connect() {

    final XulDialog waitDialog = (XulDialog) document.getElementById(ANON_WAIT_DIALOG);
    try {
      if (selectedSchemaProvider == null) {
        throw new AggDesignerException(Messages.getString("select_olap_model"));
      }

      workspace.setApplicationUnlocked(false);

      new Thread() {
        @Override
        public void run() {
          try {
            while (waitDialog.isHidden()) {
              Thread.sleep(300);
            }
            ConnectionController.this.schema = selectedSchemaProvider.loadSchema(connectionModel.getCubeName());
          } catch (Exception e) {
            //consume, schema will be null which is checked outside of this thread
            logger.error("Error loading schema: ", e);
          } finally {
            waitDialog.hide();
          }
        }
      }.start();

      waitDialog.show();
      if (schema == null) {
        throw new AggDesignerException("Error loading Schema");
      }
      connectionModel.setSchema(schema);
      outputService.init(schema);
      connectionModel.setSchemaUpToDate(true);

      // don't unlock app until everything has succeeded.
      workspace.setApplicationUnlocked(true);
      hideConnectionDialog();
    } catch (Exception e) {
      logger.error("Unable to connect", e);
      if (!waitDialog.isHidden()) {
        waitDialog.hide();
      }

      XulDialog connectErrorDialog = (XulDialog) document.getElementById(CONNECT_ERROR_DIALOG);
      Assert.notNull(connectErrorDialog, "missing element from document");
      XulTextbox connectErrorDialogMessage = (XulTextbox) document.getElementById("connectErrorDialogMessage");
      Assert.notNull(connectErrorDialogMessage, "missing element from document");
      connectErrorDialogMessage.setValue(e.getLocalizedMessage());
      connectErrorDialog.show();

    }
  }

  @RequestHandler
  public void connectErrorDialogDismiss() {
    XulDialog connectErrorDialog = (XulDialog) document.getElementById(CONNECT_ERROR_DIALOG);
    Assert.notNull(connectErrorDialog, "missing element from document");
    if (!connectErrorDialog.isHidden()) {
      connectErrorDialog.hide();
    }
  }

  @RequestHandler
  public void showConnectionDialog() {
    logger.debug("In Thread showing mondrian dialog");
    XulDialog connectionDialog = (XulDialog) document.getElementById(CONNECTION_DIALOG);
    connectionDialog.show();
  }

  @RequestHandler
  public void hideConnectionDialog() {
    XulDialog connectionDialog = (XulDialog) document.getElementById(CONNECTION_DIALOG);
    connectionDialog.hide();
  }

  private List<String> cubeNames = null;

  /**
   * Applies the schema provided by the {@link SchemaProviderUiExtension} and populates
   * the cube selector widget with a list of available cubes in the schema.
   * @throws XulException
   */
  @RequestHandler
  public void apply() throws XulException {
    final XulDialog waitDialog = (XulDialog) document.getElementById(ANON_WAIT_DIALOG);

    logger.debug("starting thread");
    new Thread() {
      @Override
      public void run() {
        // don't proceed until the wait dialog is shown
        while (waitDialog.isHidden()) {
          try {
            logger.debug("waiting for wait dialog to show");
            sleep(500);
          } catch (InterruptedException e) {
            logger.error("an exception occurred", e);
            return;
          }
        }

        logger.debug("apply is running in separate thread");
        try {
          setSelectedSchemaProvider();
          if (selectedSchemaProvider == null) {
            throw new AggDesignerException(Messages.getString("select_olap_model"));
          }
          cubeNames = selectedSchemaProvider.getCubeNames();

        } catch (AggDesignerException e) {
          logger.error("Error loading OLAP schema", e);

          try {
            XulMessageBox box = (XulMessageBox) document.createElement("messagebox");
            box.setTitle("Error");
            box.setMessage(Messages.getString("Olap.apply.error"));
            box.open();
          } catch (XulException ex) {
          }

        } finally {
          logger.debug("hiding dialog if it isn't already hidden");
          waitDialog.hide();
        }

      }

    }.start();
    logger.debug("showing wait dialog");
    waitDialog.show();

    if (selectedSchemaProvider == null) {
      XulMessageBox box = (XulMessageBox) document.createElement("messagebox");
      box.setTitle("Error");
      box.setMessage("Error applying OLAP schema");
      box.open();
      return;
    }
    connectionModel.setCubeNames(cubeNames);
    connectionModel.setSelectedSchemaModel(selectedSchemaProvider.getSchemaModel());

  }
}
