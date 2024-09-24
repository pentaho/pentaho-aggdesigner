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
