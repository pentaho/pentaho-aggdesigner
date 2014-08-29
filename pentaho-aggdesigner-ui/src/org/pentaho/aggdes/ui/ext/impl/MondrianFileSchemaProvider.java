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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.ui.model.SchemaModel;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;
import org.pentaho.ui.xul.stereotype.Controller;

@Controller
public class MondrianFileSchemaProvider extends AbstractMondrianSchemaProvider {

  private static final Log logger = LogFactory.getLog(MondrianFileSchemaProvider.class);
  
  private MondrianFileSchemaModel model = new MondrianFileSchemaModel();
  
  @Override
  public String getOverlayPath() {
    return "org/pentaho/aggdes/ui/resources/mondrianFileSchemaProvider.xul";
  }

  @Override
  public void onLoad() {
    super.onLoad();
    
    bindingFactory.createBinding("mondrianSelector", "selected", this, "selected");
    
    bindingFactory.createBinding(this, "selectedEnabled", "mondrianSchemaFileName", "!disabled");
    bindingFactory.createBinding(this, "selectedEnabled", "fileSelector", "!disabled");
    bindingFactory.createBinding(this, "mondrianSchemaFilename", "mondrianSchemaFileName", "value");
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    bindingFactory.createBinding(this, "enabled", "mondrianSelector", "!disabled");
    
    addPropertyChangeListener("mondrianSchemaFilename", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        String val = (String)evt.getNewValue();
        setSchemaDefined(!StringUtils.isEmpty(val));
      }
    });
    
    BindingConvertor<Boolean, Boolean> isDefinedConverter = new BindingConvertor<Boolean, Boolean>(){

      @Override
      public Boolean sourceToTarget(Boolean value) {
        if(Boolean.TRUE.equals(value)) {
          return !StringUtils.isEmpty(getMondrianSchemaFilename());
        }
        return false;
      }

      @Override
      public Boolean targetToSource(Boolean value) {
        return null;
      }
    };
    
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    bindingFactory.createBinding(this, "selected", this, "schemaDefined", isDefinedConverter);
    
    // special binding based on type of database meta
    BindingConvertor<Boolean, Boolean> converter = new BindingConvertor<Boolean, Boolean>() {
      @Override
      public Boolean sourceToTarget(Boolean value) {
        DatabaseMeta databaseMeta = connectionModel.getDatabaseMeta();
        boolean disabled = connectionModel.isSchemaLocked();
        
        if (disabled && isSelected()) {
          setSelected(false);
        }
        return disabled;
      }
      @Override
      public Boolean targetToSource(Boolean value) {
        // TODO Auto-generated method stub
        return null;
      }
    };
    bindingFactory.createBinding(connectionModel, "schemaSourceSelectionEnabled", "mondrianSelector", "disabled", converter);
    
  }

  public String getMondrianSchemaFilename() {
    return model.getMondrianSchemaFilename();
  }
  public void setMondrianSchemaFilename(String mondrianSchemaFilename) {
    String oldVal = model.getMondrianSchemaFilename();
    model.setMondrianSchemaFilename(mondrianSchemaFilename);
    this.firePropertyChange("mondrianSchemaFilename", oldVal, mondrianSchemaFilename);
  }

  public void chooseFile() throws AggDesignerException {
    try {
      XulFileDialog fc = (XulFileDialog) document.createElement("filedialog");

      RETURN_CODE retVal;
      if (getLastFile() != null) {
        retVal = fc.showOpenDialog(getLastFile());
      } else {
        retVal = fc.showOpenDialog();
      }

      if (retVal == RETURN_CODE.OK) {
        File selectedFile = (File) fc.getFile();
        setLastFile(selectedFile);
        setMondrianSchemaFilename(((File)fc.getFile()).getAbsolutePath());
        
      }
    } catch (Exception e) {
      logger.error("Error showing file chooser", e);
      throw new AggDesignerException(e);
    }
  }
  
  private String getFileContents(File file) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      return sb.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Returns the last opened location in the file chooser.
   */
  protected File getLastFile() {
    File file = new File(".schemaInfo");
    if (file.exists()) {
      String path = getFileContents(file).replaceAll("\n", "");
      return new File(path);
    } else {
      return null;
    }
  }

  protected void setLastFile(File f) {
    try {
      File file = new File(".schemaInfo");
      PrintWriter pw = new PrintWriter(new FileWriter(file));
      pw.println(f.getAbsolutePath());
      pw.close();
    } catch (Exception e) {
      logger.error("could not write last file path", e);
    }
  }

  public boolean supportsSchemaModel(SchemaModel schemaModel) {
    return (schemaModel instanceof MondrianFileSchemaModel);
  }
  
  public MondrianFileSchemaModel getSchemaModel() {
    return model;
  }

  public void setSchemaModel(SchemaModel model) {
    MondrianFileSchemaModel schemaModel = (MondrianFileSchemaModel)model;
    String oldVal = this.model.getMondrianSchemaFilename();
    this.model = schemaModel;
    firePropertyChange("mondrianSchemaFilename", oldVal, schemaModel.getMondrianSchemaFilename());
  }

  @Override
  public String getName(){
    return "mondrianFileSchemaProvider";
  }

  public void reset() {
    setSchemaModel(new MondrianFileSchemaModel());
    //force an event to fire
    this.firePropertyChange("mondrianSchemaFilename", "", null);
  }

  @Override
  public void onUnload() {
    
        // TODO Auto-generated method stub 
      
  }
  
 

}

  
