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

package org.pentaho.aggdes.ui.model;

public interface SchemaModel {
  public long recalculateSchemaChecksum();
  public void setSchemaChecksum(long schemaChecksum);
  public long getSchemaChecksum();
  public String getCubeName();
  public void setCubeName(String cubeName);
  public boolean schemaExists();
  public String getSchemaDoesNotExistErrorMessage();
}
