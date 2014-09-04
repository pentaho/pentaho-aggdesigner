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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.springmodules.validation.bean.BeanValidator;

public class ConnectionModelStub extends XulEventSourceAdapter implements ConnectionModel{

  private BeanValidator beanValidator;
  private String cubeName;
  private List<String> cubeNames = new ArrayList<String>();
  private DatabaseMeta databaseMeta;
  private Schema schema;
  
  public ConnectionModelStub(Schema schema){
    this.schema = schema;
  }
  
  public BeanValidator getBeanValidator() {
    return beanValidator;
  }

  public String getCubeName() {
    return cubeName;
  }

  public List<String> getCubeNames() {
    return cubeNames;
  }

  public DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }

  public String getDatabaseName() {
    return databaseMeta.getName();
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }
  
  public Schema getSchema() {
    return schema;
  }

  public List<SchemaProviderUiExtension> getSchemaProviders() {
    return null;
  }

  public SchemaProviderUiExtension getSelectedSchemaProvider() {
    return null;
  }

  public boolean isConnectEnabled() {
    return false;
  }

  public boolean isSchemaSourceSelectionEnabled() {
    return false;
  }

  public void loadSchema() throws AggDesignerException {
      
  }

  public void setBeanValidator(BeanValidator beanValidator) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setConnectEnabled(boolean b) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setCubeName(String cubeName) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setCubeNames(List<String> cubeNames) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setSchemaSourceSelectionEnabled(boolean b) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setDatabaseMeta(DatabaseMeta databaseMeta) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setSchemaProviders(List<SchemaProviderUiExtension> schemaProviders) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setSelectedSchemaProvider(SchemaProviderUiExtension schemaProvider) {
    
        // TODO Auto-generated method stub 
      
  }

  public String getSchemaName() {
    return null;
  }

  public boolean getSchemaUpToDate() {
    return false;  
  }

  public void setSchemaName(String schemaName) {
    
        // TODO Auto-generated method stub 
      
  }

  public void setSchemaUpToDate(boolean upToDate) {
    
        // TODO Auto-generated method stub 
      
  }

  public void apply() throws AggDesignerException {
    
  }

  public boolean isCubeSelectionEnabled() {
    return false;
  }

  public SchemaModel getSelectedSchemaModel() {
    // TODO Auto-generated method stub
    return null;
  }

  public void setSelectedSchemaModel(SchemaModel schemaModel) {
    // TODO Auto-generated method stub
    
  }

  public void reset() {
    // TODO Auto-generated method stub
    
  }

  public boolean isApplySchemaSourceEnabled() {
    // TODO Auto-generated method stub
    return false;
  }

  public void setApplySchemaSourceEnabled(boolean b) {
    // TODO Auto-generated method stub
    
  }

  public void lockDownSchema() {
    
        // TODO Auto-generated method stub 
      
  }

  public boolean isSchemaLocked() {
    return false;  
  }

  public void setSchemaLocked(boolean locked) {
      
  }

}

  
