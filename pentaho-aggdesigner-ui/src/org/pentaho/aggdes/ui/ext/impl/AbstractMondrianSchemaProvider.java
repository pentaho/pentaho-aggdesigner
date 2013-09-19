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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.ui.ext.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mondrian.olap.MondrianDef;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;
import org.pentaho.aggdes.AggDesignerException;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.model.Schema;
import org.pentaho.aggdes.model.ValidationMessage;
import org.pentaho.aggdes.model.mondrian.MondrianSchemaLoader;
import org.pentaho.aggdes.model.mondrian.validate.ValidationHelper;
import org.pentaho.aggdes.ui.ext.AbstractUiExtension;
import org.pentaho.aggdes.ui.ext.SchemaProviderUiExtension;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.containers.XulDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class AbstractMondrianSchemaProvider extends AbstractUiExtension implements SchemaProviderUiExtension {
  
  private static final Log logger = LogFactory.getLog(AbstractMondrianSchemaProvider.class);
  
  protected List<String> validatorList;
  protected ConnectionModel connectionModel;
  protected MondrianSchemaLoader mondrianSchemaLoader;
  protected boolean continueOnValidationErrors = false;
  protected boolean selected = false;
  protected boolean enabled = false;
  protected boolean schemaDefined = false;
  protected boolean selectedEnabled = false;
  
  protected BindingFactory bindingFactory;

  public void onLoad(){
    bindingFactory.setDocument(document);
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    bindingFactory.createBinding(connectionModel, "schemaSourceSelectionEnabled", this, "enabled");
    bindingFactory.setBindingType(Binding.Type.BI_DIRECTIONAL);
  }

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    bindingFactory.setDocument(document);
    this.bindingFactory = bindingFactory;
  }
  
  public void setConnectionModel(ConnectionModel connectionModel) {
    this.connectionModel = connectionModel;
  }
  
  public void setValidatorList(List<String> validatorList) {
    this.validatorList = validatorList;
  }
  
  public void setMondrianSchemaLoader(MondrianSchemaLoader mondrianSchemaLoader) {
    this.mondrianSchemaLoader = mondrianSchemaLoader;
  }
  
  public void setContinueOnValidationErrors(boolean continueOnValidationErrors) {
    this.continueOnValidationErrors = continueOnValidationErrors;
  }
  
  public boolean isSchemaDefined() {
    return schemaDefined;
  }

  public void setSchemaDefined(boolean schemaDefined) {
    boolean prev = this.schemaDefined;
    this.schemaDefined = schemaDefined;
    logger.debug(getName()+".setSchemaDefined("+this.schemaDefined+"): oldVal="+prev);
    // force a fire by setting previous value to null;
    // force since schemaDefined can go from true to true in the case of a non-null filename to another non-null valid filename 
    firePropertyChange("schemaDefined", null, schemaDefined);
  }
  
  public abstract String getMondrianSchemaFilename() throws AggDesignerException;
  
  public boolean doValidation(final String cubeName) {
    final XulDialog dialog = (XulDialog) document.getElementById("validationProgressDialog");
    Assert.notNull(dialog, "could not find element with id '" + "validationProgressDialog" + "'");
    final List<ValidationMessage> validationMessages = new Vector<ValidationMessage>();
    new Thread() {

      @Override
      public void run() {
        try {
          while (dialog.isHidden()) {
            sleep(500);
          }
          
          DatabaseMeta dbMeta = connectionModel.getDatabaseMeta();
          final String mondrianConnectionUrl = MessageFormat.format(
              "Provider={0};Jdbc={1};JdbcUser={2};JdbcPassword={3};Catalog={4};JdbcDrivers={5}", "Mondrian", dbMeta.getURL(), dbMeta
                  .getUsername(), dbMeta.getPassword(), "file:" + getMondrianSchemaFilename(), 
                  dbMeta.getDriverClass());
          Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();

          parameterValues.put(mondrianSchemaLoader.getParameters().get(0), mondrianConnectionUrl);
          parameterValues.put(mondrianSchemaLoader.getParameters().get(1), cubeName);
          
          StringBuilder validatorClassString = new StringBuilder();
          for (String validatorClass : validatorList) {
            if (validatorClassString.length() > 0) {
              validatorClassString.append(","); //$NON-NLS-1$
            }
            validatorClassString.append(validatorClass);
          }
          
          parameterValues.put(mondrianSchemaLoader.getParameters().get(2), validatorClassString.toString());
          List<ValidationMessage> messages = mondrianSchemaLoader.validateSchema(parameterValues);
          XulDialog dialog = (XulDialog) document.getElementById("validationProgressDialog");
          Assert.notNull(dialog, "could not find element with id '" + "validationProgressDialog" + "'");
          validationMessages.addAll(messages);
          dialog.hide();
          
        } catch (Exception e) {
          if (logger.isErrorEnabled()) {
            logger.error("an exception occurred", e);
          }
          dialog.hide();
        }

      }

    }.start();
    dialog.show();

    // dialog.hide() in validationDone unblocks the above hide
    
    final XulDialog validationDialog1 = (XulDialog) document.getElementById("validationDialog1");
    final XulTextbox textbox = (XulTextbox) document.getElementById("validationMessages");
    textbox.setValue(ValidationHelper.messagesToString(validationMessages)); 
    validationDialog1.show();
    // reset
    boolean hasErrors = ValidationHelper.hasErrors(validationMessages);
    return hasErrors;
  }

  
  public Schema loadSchema(String cubeName) throws AggDesignerException{
    boolean validationHasErrors = doValidation(cubeName);
    if (validationHasErrors && !continueOnValidationErrors) {
      return null;
    }
    
    try {
      DatabaseMeta dbMeta = connectionModel.getDatabaseMeta();
      final String mondrianConnectionUrl = MessageFormat.format(
          "Provider={0};Jdbc={1};JdbcUser={2};JdbcPassword={3};Catalog={4};JdbcDrivers={5}", "Mondrian", dbMeta.getURL(), dbMeta
              .getUsername(), dbMeta.getPassword(), "file:" + getMondrianSchemaFilename(), 
              dbMeta.getDriverClass());
      
      Map<Parameter, Object> parameterValues = new HashMap<Parameter, Object>();

      parameterValues.put(mondrianSchemaLoader.getParameters().get(0), mondrianConnectionUrl);
      parameterValues.put(mondrianSchemaLoader.getParameters().get(1), cubeName);

      Schema newSchema = mondrianSchemaLoader.createSchema(parameterValues);
      return newSchema;
    } catch(KettleDatabaseException e){
      throw new AggDesignerException("Error loading Schema from Mondrian file",e);
    }
  }
  
  public List<String> getCubeNames() throws AggDesignerException{
    List<String> cubeNames;
    try {
      final Parser xmlParser = XOMUtil.createDefaultParser();
      cubeNames = new ArrayList<String>();
      cubeNames.add(Messages.getString("select_cube"));
      
      String fileName = getMondrianSchemaFilename();
      if(StringUtils.isEmpty(fileName)){
        throw new AggDesignerException(Messages.getString("mondrian_file_null"));
      }
      
      File mondrianSchema = new File(getMondrianSchemaFilename());
      if (mondrianSchema != null && mondrianSchema.exists() && mondrianSchema.isFile()) {
        FileReader schemaReader = new FileReader(mondrianSchema);
        final DOMWrapper def = xmlParser.parse(schemaReader);
        MondrianDef.Schema tSchema = new MondrianDef.Schema(def);

        for (MondrianDef.Cube cube : tSchema.cubes) {
          logger.debug("cube: " + cube.name);
          cubeNames.add(cube.name);
        }
        connectionModel.setSchemaName(tSchema.name);
      } else {
        throw new AggDesignerException("Error loading Mondrian Schema.");
      }
    } catch (FileNotFoundException e) {
      throw new AggDesignerException("Schema source not found.", e);
    } catch (XOMException e) {
      throw new AggDesignerException("Error parsing schema source.", e);
    }
    return cubeNames;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    boolean prev = this.enabled;
    this.enabled = enabled;
    
    //making these always fire. Catching the cycle by making the binding's one-way
    firePropertyChange("enabled", null, enabled);

    setSelectedEnabled(this.enabled && this.selected);
  }

  public boolean isSelected() {
    return selected;
  }
  
  public void setSelected(boolean selected) {
    boolean prev = this.selected;
    this.selected = selected;   
    logger.debug(getName()+".setSelected("+selected+")");
    firePropertyChange("selected", prev, selected);

    setSelectedEnabled(this.enabled && this.selected);
    
  }
  
  //Composite property of "selected" and "enabled"
  public boolean isSelectedEnabled() {
    return selectedEnabled;
  }

  public void setSelectedEnabled(boolean selectedEnabled) {
    boolean prev = this.selectedEnabled;
    this.selectedEnabled = selectedEnabled;
    this.firePropertyChange("selectedEnabled", prev, selectedEnabled);
  }

}
