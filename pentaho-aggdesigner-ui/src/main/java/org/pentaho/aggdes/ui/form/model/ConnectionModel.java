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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
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

  
