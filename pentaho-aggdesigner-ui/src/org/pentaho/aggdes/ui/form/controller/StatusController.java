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
package org.pentaho.aggdes.ui.form.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;

@Controller
public class StatusController extends AbstractXulEventHandler {

  private ConnectionModel connectionModel;

  private static final Log logger = LogFactory.getLog(StatusController.class);

  private StatusModel statusModel = new StatusModel();

  public void onLoad() {

    Binding bind = new Binding(connectionModel, "cubeName", statusModel, "cubeName"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    getXulDomContainer().addBinding(bind);

    bind = new Binding(connectionModel, "schemaName", statusModel, "schemaName"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    getXulDomContainer().addBinding(bind);

    bind = new Binding(connectionModel, "databaseName", statusModel, "databaseName"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    getXulDomContainer().addBinding(bind);

    bind = new Binding(statusModel, "statusMessage", document.getElementById("statusMessage"), "value"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    getXulDomContainer().addBinding(bind);

    bind = new Binding(connectionModel, "schema", statusModel, "connected"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    bind.setConversion(new BindingConvertor<Schema, Boolean>(){
      public Boolean sourceToTarget(Schema value) {
          return (value != null);
      }

      public Schema targetToSource(Boolean value) {
        return null; //Not impl
      }
    });
    getXulDomContainer().addBinding(bind);

    bind = new Binding(statusModel, "connected", document.getElementById("connectionImage"), "image"); //$NON-NLS-1$ //$NON-NLS-2$
    bind.setBindingType(Binding.Type.ONE_WAY);
    bind.setConversion(new BindingConvertor<Boolean, String>() {

      @Override
      public String sourceToTarget(Boolean connected) {
        return (connected) ? "images/ok.png" : "images/disconnected.png";
      }

      @Override
      public Boolean targetToSource(String arg0) {
        return null;    //one-way, not impl.
      }
    });
    getXulDomContainer().addBinding(bind);

  }

  /* ========================================================== *
   *                       Spring Injected                      *
   * ========================================================== */
  public ConnectionModel getConnectionModel() {
    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {
    this.connectionModel = connectionModel;
  }

  /* ========================================================== *
   *                     POJO Status Model                      *
   * ========================================================== */
  public class StatusModel extends XulEventSourceAdapter {

    private String databaseName;

    private String schemaName;

    private String cubeName;

    private boolean connected = false;

    public String getStatusMessage() {
      return String.format("%s      %s      %s", //$NON-NLS-1$
          (!StringUtils.isEmpty(databaseName)) ? Messages.getString("database") + ": " +databaseName : "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          (!StringUtils.isEmpty(schemaName)) ? Messages.getString("schema") + ": " + schemaName : "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          (!StringUtils.isEmpty(cubeName)) ? Messages.getString("cube") + ": " + cubeName : "" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      );
    }

    public String getDatabaseName() {
      return databaseName;
    }

    public void setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
      firePropertyChange("statusMessage", null, getStatusMessage()); //$NON-NLS-1$
    }

    public String getSchemaName() {
      return schemaName;
    }

    public void setSchemaName(String schemaName) {
      this.schemaName = schemaName;
      firePropertyChange("statusMessage", null, getStatusMessage()); //$NON-NLS-1$
    }

    public String getCubeName() {
      return cubeName;
    }

    public void setCubeName(String cubeName) {
      if (cubeName == null || cubeName.equals(Messages.getString("select_cube"))) { //$NON-NLS-1$
        cubeName = "";
      }
      this.cubeName = cubeName;
      firePropertyChange("statusMessage", null, getStatusMessage()); //$NON-NLS-1$
    }
    public void setConnected(boolean bool) {
      this.connected = bool;
      firePropertyChange("connected", null, bool); //$NON-NLS-1$
    }

    public boolean getConnected() {
      return this.connected;
    }
  }

}
