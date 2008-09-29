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
package org.pentaho.aggdes.ui.form.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.stereotype.FormModel;

@FormModel
public class ConnectionModelImpl extends XulEventSourceAdapter implements ConnectionModel {

  private static final Log logger = LogFactory.getLog(ConnectionModelImpl.class);

  private DatabaseMeta databaseMeta;

  private List<String> cubeNames;
  
  private String schemaName;

  private boolean connectEnabled = false;

  private boolean schemaSourceSelectionEnabled = false;
  
  private boolean schemaUpToDate = false;
  
  private String databaseName;
  
  private Schema schema;

  private SchemaModel schemaModel;
  
  private boolean applySchemaSourceEnabled = false;

  private boolean schemaLocked = false;
  
  public void setSchema(Schema schema) {
    this.schema = schema;
    this.firePropertyChange("schema", null, schema);
  }
  
  public Schema getSchema() {
    return schema;
  }

  public String getDatabaseName() {
    return (databaseMeta != null) ? databaseMeta.getDatabaseName() : null;
  }

  public void setDatabaseMeta(DatabaseMeta databaseMeta) {
    this.databaseMeta = databaseMeta;
    setSchemaSourceSelectionEnabled(databaseMeta != null);
    setDatabaseName((databaseMeta != null)? databaseMeta.getName() : "");
    
    // write out last database info to disk, if not null
    if (databaseMeta != null) {
      try {
        File file = new File(".databaseMetaInfo");
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println(databaseMeta.getXML());
        pw.close();
        logger.debug(databaseMeta.getURL());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public DatabaseMeta getDatabaseMeta() {
    if (this.databaseMeta == null) {
      File file = new File(".databaseMetaInfo");
      if (file.exists()) {
        try {
          this.databaseMeta = new DatabaseMeta(getFileContents(file));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return this.databaseMeta;
  }

  // TODO: Once moving to a wizard, the UIExtension should handle the selecting of the cube
  
  public String getCubeName() {
    if (schemaModel == null) {
      return null;
    }
    return schemaModel.getCubeName();
  }

  public void setCubeName(String cubeName) {
    if (cubeName != null && cubeName.equals(Messages.getString("select_cube"))) {
      cubeName = null;
    }
    Object oldVal = null;
    if (schemaModel != null) {
      oldVal = schemaModel.getCubeName();
      schemaModel.setCubeName(cubeName);
    }
    if (cubeName == null) {
      cubeName = Messages.getString("select_cube");
    }
    firePropertyChange("cubeName", oldVal, cubeName);
    validate();
  }

  private void validate() {
    setConnectEnabled(databaseMeta != null && getCubeName() != null);
  }

  public List<String> getCubeNames() {
    return cubeNames;
  }
  
  public boolean isCubeSelectionEnabled(){
    return (cubeNames != null && cubeNames.size() > 0);
  }

  public void setCubeNames(List<String> cubeNames) {
    List<String> prevNames = this.cubeNames;
    boolean prevEnabled = isCubeSelectionEnabled();
    this.cubeNames = cubeNames;
    firePropertyChange("cubeSelectionEnabled", prevEnabled, isCubeSelectionEnabled());
    
    // Binding Hack
    if (cubeNames == null) {
      cubeNames = new ArrayList<String>();
      cubeNames.add(Messages.getString("select_cube"));
    }
    firePropertyChange("cubeNames", prevNames, cubeNames);
  }

  public void setConnectEnabled(boolean b) {
    this.connectEnabled = b;
    firePropertyChange("connectEnabled", null, b);
  }

  public boolean isConnectEnabled() {
    return connectEnabled;
  }

  public void setSchemaSourceSelectionEnabled(boolean b) {
    this.schemaSourceSelectionEnabled = b;
    firePropertyChange("schemaSourceSelectionEnabled", null, b);
  }

  public boolean isSchemaSourceSelectionEnabled() {
    return schemaSourceSelectionEnabled;
  }

  public boolean isApplySchemaSourceEnabled() {
    return applySchemaSourceEnabled;
  }

  public void setApplySchemaSourceEnabled(boolean applySchemaSourceEnabled) {
    Object oldVal = this.applySchemaSourceEnabled;
    this.applySchemaSourceEnabled = applySchemaSourceEnabled;
    firePropertyChange("applySchemaSourceEnabled", oldVal, applySchemaSourceEnabled);
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    String oldSchemaName = this.schemaName;
    this.schemaName = schemaName;
    firePropertyChange("schemaName", oldSchemaName, schemaName);
  }

  public boolean getSchemaUpToDate() {
    return schemaUpToDate;
  }

  public void setSchemaUpToDate(boolean upToDate) {
    this.schemaUpToDate = upToDate;
    firePropertyChange("schemaUpToDate", null, schemaUpToDate);
  }

  public void setDatabaseName(String databaseName) {
    String oldVal = this.databaseName;
    this.databaseName = databaseName;
    firePropertyChange("databaseName", oldVal, databaseName);
  }

  public SchemaModel getSelectedSchemaModel() {
    return schemaModel;
  }

  public void setSelectedSchemaModel(SchemaModel schemaModel) {
    this.schemaModel = schemaModel;
    firePropertyChange("selectedSchemaModel", null, schemaModel);
  }

  public void reset() {
    setCubeName(null);
    setCubeNames(null);
    setSelectedSchemaModel(null);
    setDatabaseMeta(null);
  }
  
  private String getFileContents(File file) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public boolean isSchemaLocked() {
    return schemaLocked;
  }

  public void setSchemaLocked(boolean locked) {
    boolean oldVal = this.schemaLocked;
    this.schemaLocked = locked;
    
    this.firePropertyChange("schemaLocked", oldVal, this.schemaLocked);
  }

}

       