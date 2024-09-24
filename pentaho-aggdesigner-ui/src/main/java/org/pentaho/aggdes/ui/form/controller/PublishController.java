/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.aggdes.ui.form.controller;

import java.io.File;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.mondrian.MondrianSchema;
import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.mondrian.publish.Messages;
import org.pentaho.mondrian.publish.PublishSchemaPluginParent;
import org.pentaho.mondrian.publish.PublishToServerCommand;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;

/**
 * This class handles the publishing of a Mondrian Schema
 * 
 * TODO:
 *  - investigate Schema distribution across components, consider adding it to workspace?
 *  
 * @author Will Gorman (wgorman@pentaho.com)
 *
 */
@Controller
public class PublishController extends AbstractXulEventHandler implements PublishSchemaPluginParent {

  private static final Log logger = LogFactory.getLog(PublishController.class);
  
  ExportHandler exportHandler;
  
  private Workspace workspace;
  
  private ConnectionModel connectionModel;

  public void setWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }

  public void setExportHandler(ExportHandler exportHandler) {
    this.exportHandler = exportHandler;
  }

  public void publishSchema() throws XulException {
    // first, determine if new schema has been written
    
    // If we're not dealing with a MondrianFileSchemaModel object, something 
    // has gone wrong with the UI application state.
    if (!(connectionModel.getSelectedSchemaModel() instanceof MondrianFileSchemaModel)) {
      XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setMessage("Inconsistent application state: Only MondrianFileSchemaModel should call into this method");
      msgBox.open();
      logger.error("Inconsistent application state: Only MondrianFileSchemaModel should call into this method");
      return;
    }
    
    File schemaFile = new File(((MondrianFileSchemaModel)connectionModel.getSelectedSchemaModel()).getMondrianSchemaFilename());

    // if not, display save options
    if (!connectionModel.getSchemaUpToDate()) {

      XulMessageBox msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setTitle(Messages.getString("SchemaModifiedWarning.Title"));
      msgBox.setMessage(Messages.getString("SchemaModifiedWarning.Message"));
      msgBox.setButtons(new String[] {"Yes", "No", "Cancel"});
      int option = msgBox.open();
      
      if (option == 0) {
        schemaFile = exportHandler.saveOlap();
      } else if (option == 2) {
        return;
      }
    }

    if (schemaFile != null) {
      // second, publish schema 
      PublishToServerCommand command = new PublishToServerCommand();
      command.execute(this);
    }
  }

  public JFrame getFrame() {
    return (JFrame)document.getRootElement().getManagedObject();
  }

  public File getSchemaFile() {
    return new File(((MondrianFileSchemaModel)connectionModel.getSelectedSchemaModel()).getMondrianSchemaFilename());
  }

  public String getSchemaName() {
    return ((MondrianSchema) connectionModel.getSchema()).getRolapConnection().getSchema().getName();
  }

  public String getProperty(String name) {
    return workspace.getWorkspaceProperty(name);
  }

  public void setProperty(String name, String value) {
    workspace.setWorkspaceProperty(name, value);
  }

  public void storeProperties() {
    workspace.storeWorkspaceProperties();
  }

  public void setConnectionModel(ConnectionModel connectionModel) {
    this.connectionModel = connectionModel;
  }

}
