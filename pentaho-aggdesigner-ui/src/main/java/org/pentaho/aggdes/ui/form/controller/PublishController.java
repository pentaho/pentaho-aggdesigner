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
