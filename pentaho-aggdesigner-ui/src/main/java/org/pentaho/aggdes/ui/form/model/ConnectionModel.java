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

  
