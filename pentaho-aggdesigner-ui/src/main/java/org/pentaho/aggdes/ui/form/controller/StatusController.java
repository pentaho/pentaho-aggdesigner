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
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBinding;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class StatusController extends AbstractXulEventHandler {

  private ConnectionModel connectionModel;

  private static final Log logger = LogFactory.getLog(StatusController.class);

  private StatusModel statusModel = new StatusModel();

  private BindingFactory bindingFactory;

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }
  
  public void onLoad() {

    bindingFactory.setDocument(document);
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    
    bindingFactory.createBinding(connectionModel, "cubeName", statusModel, "cubeName"); //$NON-NLS-1$ //$NON-NLS-2$
    bindingFactory.createBinding(connectionModel, "schemaName", statusModel, "schemaName"); //$NON-NLS-1$ //$NON-NLS-2$
    bindingFactory.createBinding(connectionModel, "databaseName", statusModel, "databaseName"); //$NON-NLS-1$ //$NON-NLS-2$
    bindingFactory.createBinding(statusModel, "statusMessage", document.getElementById("statusMessage"), "value"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    
    
    bindingFactory.createBinding(connectionModel, "schema", statusModel, "connected", new BindingConvertor<Schema, Boolean>(){
        public Boolean sourceToTarget(Schema value) {
          return (value != null);
        }
  
        public Schema targetToSource(Boolean value) {
          return null; //Not impl
        }
    }); //$NON-NLS-1$ //$NON-NLS-2$
    

    bindingFactory.createBinding(statusModel, "connected", document.getElementById("connectionImage"), "image", new BindingConvertor<Boolean, String>() {

      @Override
      public String sourceToTarget(Boolean connected) {
        return (connected) ? "images/ok.png" : "images/disconnected.png";
      }

      @Override
      public Boolean targetToSource(String arg0) {
        return null;    //one-way, not impl.
      }
    }); //$NON-NLS-1$ //$NON-NLS-2$

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
