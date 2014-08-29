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

package org.pentaho.aggdes.ui.form.model;

import java.util.List;

import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.XulEventSource;

public interface ConnectionModel extends XulEventSource{
 
  public void setSchema(Schema schema);
  
  public Schema getSchema();
  
  public String getDatabaseName();

  public void setDatabaseMeta(DatabaseMeta databaseMeta);

  public DatabaseMeta getDatabaseMeta();
  
  public String getCubeName();

  public String getSchemaName();
  
  public void setSchemaName(String schemaName);
  
  public void setCubeName(String cubeName);
  
  public List<String> getCubeNames();

  public void setCubeNames(List<String> cubeNames);
  
  public void setConnectEnabled(boolean b);
  
  public boolean isConnectEnabled();
  
  public void setSchemaSourceSelectionEnabled(boolean b);

  public boolean isSchemaSourceSelectionEnabled();
  
  public void setApplySchemaSourceEnabled(boolean b);
  
  public boolean isApplySchemaSourceEnabled();
  
  public boolean getSchemaUpToDate();
  
  public void setSchemaUpToDate(boolean upToDate);
  
  public boolean isCubeSelectionEnabled();
  
  public void setSelectedSchemaModel(SchemaModel schemaModel);
  
  public SchemaModel getSelectedSchemaModel();
  
  public void reset();
  
  public boolean isSchemaLocked();
  
  public void setSchemaLocked(boolean locked);
}

  
