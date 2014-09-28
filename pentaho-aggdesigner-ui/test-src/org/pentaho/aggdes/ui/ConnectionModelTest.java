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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModelImpl;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;


public class ConnectionModelTest {

    public ConnectionModelTest() {
    try {
        KettleClientEnvironment.init();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  private ConnectionModel connectionModel;

  @Before
  public void init() {
    connectionModel = new ConnectionModelImpl();
  }

  @Test
  public void testSchemaSourceSelectionEnablement() {
    assertFalse(connectionModel.isSchemaSourceSelectionEnabled());
    connectionModel.setDatabaseMeta(new DatabaseMeta());
    assertTrue(connectionModel.isSchemaSourceSelectionEnabled());
  }

  @Test
  public void testApplySchemaSourceEnablement() {
    assertFalse(connectionModel.isApplySchemaSourceEnabled());
    //the controller will bind the applySchemaSourceEnabled property to the schemaDefined property of the extension
    //we will just mimic this here
    connectionModel.setApplySchemaSourceEnabled(true);
    assertTrue(connectionModel.isApplySchemaSourceEnabled());
  }

  @Test
  public void testCubeSelectionEnablement() {
    assertFalse(connectionModel.isCubeSelectionEnabled());

  }

  @Test
  public void testConnectEnablement() {
    assertFalse(connectionModel.isConnectEnabled());

  }

  @Test
  public void testDatabaseName() {
    assertEquals(connectionModel.getDatabaseName(), null);
    connectionModel.setDatabaseMeta(new DatabaseMeta());
    assertEquals(connectionModel.getDatabaseName(), "");
    connectionModel.getDatabaseMeta().setDBName("temp_name");
    assertEquals(connectionModel.getDatabaseName(), "temp_name");
  }

  @Test
  public void testSchemaName() {
    final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
    assertNull(connectionModel.getSchemaName(), null);
    connectionModel.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        events.add(evt);
      }
    });

    connectionModel.setSchemaName("test_schema");
    assertEquals(events.size(), 1);
    assertNull(events.get(0).getOldValue());
    assertEquals(events.get(0).getNewValue(), "test_schema");
    assertEquals(events.get(0).getPropertyName(), "schemaName");

    assertEquals(connectionModel.getSchemaName(), "test_schema");
  }

  @Test
  public void testSchemaUpToDate() {
    final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
    assertEquals(connectionModel.getSchemaUpToDate(), false);
    connectionModel.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        events.add(evt);
      }
    });

    connectionModel.setSchemaUpToDate(true);
    assertEquals(events.size(), 1);
    // todo: should this be false instead of null?
    assertEquals(events.get(0).getOldValue(), null);
    assertEquals(events.get(0).getNewValue(), true);
    assertEquals(events.get(0).getPropertyName(), "schemaUpToDate");

    assertEquals(connectionModel.getSchemaUpToDate(), true);
  }

  @Test
  public void testGetDatabaseMeta() {
    // this should generate the basic database meta object
    DatabaseMeta meta = new DatabaseMeta();
    meta.setName("test");
    connectionModel.setDatabaseMeta(meta);

    // this should reset the database meta object
    connectionModel.setDatabaseMeta(null);

    // this should load the database meta object from disk
    DatabaseMeta newmeta = connectionModel.getDatabaseMeta();
    assertEquals(meta, newmeta);
    assertEquals(meta.getName(), newmeta.getName());
  }

  @Test
  public void test() {
    // test that cube names is initialized as null
    assertNull(connectionModel.getCubeNames());
  }
}
