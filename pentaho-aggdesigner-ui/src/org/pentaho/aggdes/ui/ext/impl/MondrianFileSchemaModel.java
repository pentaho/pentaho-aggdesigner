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

package org.pentaho.aggdes.ui.ext.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.aggdes.ui.util.Messages;

public class MondrianFileSchemaModel implements SchemaModel {

  private long schemaChecksum;

  private String mondrianSchemaFilename;

  private String cubeName;

  public String getMondrianSchemaFilename() {
    return mondrianSchemaFilename;
  }

  public void setMondrianSchemaFilename(String mondrianSchemaFilename) {
    this.mondrianSchemaFilename = mondrianSchemaFilename;
  }

  public long recalculateSchemaChecksum() {
    if (getMondrianSchemaFilename() != null) {
      try {
        CheckedInputStream cis = new CheckedInputStream(new FileInputStream(getMondrianSchemaFilename()), new CRC32());
        byte[] buf = new byte[1024];
        while(cis.read(buf) >= 0) {
        }
        return cis.getChecksum().getValue();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    return -1;
  }

  public void setSchemaChecksum(long schemaChecksum) {
    this.schemaChecksum = schemaChecksum;
  }

  public long getSchemaChecksum() {
    return schemaChecksum;
  }

  public String getCubeName() {
    return cubeName;
  }

  public void setCubeName(String cubeName) {
    this.cubeName = cubeName;
  }

  public boolean schemaExists() {
    return mondrianSchemaFilename != null && new File(mondrianSchemaFilename).exists();
  }

  public String getSchemaDoesNotExistErrorMessage() {
    return Messages.getString("MondrianFileSchemaModel.ErrorSchemaFileDoesNotExist", mondrianSchemaFilename);
  }
}


