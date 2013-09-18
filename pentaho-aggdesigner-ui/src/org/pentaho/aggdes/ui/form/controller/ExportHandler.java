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

package org.pentaho.aggdes.ui.form.controller;

import static org.pentaho.aggdes.ui.model.AggListEvent.Type.SELECTION_CHANGED;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.output.CreateScriptGenerator;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.output.PopulateScriptGenerator;
import org.pentaho.aggdes.output.SchemaGenerator;
import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.exec.DDLExecutionCallbackService;
import org.pentaho.aggdes.ui.exec.DDLExecutionCompleteCallback;
import org.pentaho.aggdes.ui.exec.SqlExecutor;
import org.pentaho.aggdes.ui.exec.SqlExecutor.ExecutorCallback;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.AggListEvent;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBinding;
import org.pentaho.ui.xul.components.XulFileDialog;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.components.XulFileDialog.RETURN_CODE;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

//FIXME: Use XUL data binding to remove all references to XulComponents, then rename this class to ExportController
@Deprecated
@Controller
public class ExportHandler extends AbstractXulEventHandler {

  private static final Log logger = LogFactory.getLog(ExportHandler.class);

  private static final String EMPTY_STRING = ""; //$NON-NLS-1$

  private static final String SYS_PROP_LINE_SEPARATOR = "line.separator"; //$NON-NLS-1$

  private static final String ELEM_ID_FILEDIALOG = "filedialog"; //$NON-NLS-1$

  private static final String ELEM_ID_MESSAGEBOX = "messagebox"; //$NON-NLS-1$

  private static final String ELEM_ID_EXEC_PROGRESS_DIALOG = "executeDdlDmlProgressDialog"; //$NON-NLS-1$

  private static final String ELEM_ID_EXPORT_DIALOG = "export_dialog"; //$NON-NLS-1$

  private static final String ELEM_ID_PREVIEW_DIALOG = "relationalPreviewDialog"; //$NON-NLS-1$

  private static final String ELEM_ID_DDL_FIELD = "ddlField"; //$NON-NLS-1$

  private static final String ELEM_ID_DML_FIELD = "dmlField"; //$NON-NLS-1$

  private OutputService outputService;

  private SqlExecutor ddlDmlExecutor;

  private AggList aggList;
  
  private AggController aggController;

  private ConnectionModel connectionModel;

  private Workspace workspace;

  private BindingFactory bindingFactory;

  private DDLExecutionCallbackService ddlExecCallbackService;

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  @Required
  public void setOutputService(OutputService outputService) {
    this.outputService = outputService;
  }

  @Required
  public void setDdlDmlExecutor(SqlExecutor ddlDmlExecutor) {
    this.ddlDmlExecutor = ddlDmlExecutor;
  }
  
  @Required
  public void setDdlExecCallbackService(DDLExecutionCallbackService ddlExecCallbackService) {
      this.ddlExecCallbackService = ddlExecCallbackService;
  }
  
  public void onLoad() throws XulException {
    

    bindingFactory.setDocument(document);
    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    bindingFactory.createBinding(workspace, "applicationUnlocked", this, "connected"); //$NON-NLS-1$ //$NON-NLS-2$
    
  }

  public void openDialog() throws Exception {
    if (getEnabledAggs().size() == 0) {
      XulMessageBox msgBox = (XulMessageBox) document.createElement(ELEM_ID_MESSAGEBOX);
      msgBox.setMessage(Messages.getString("ExportHandler.NoAggsDefinedOrEnabled")); //$NON-NLS-1$
      msgBox.open();

      logger.info("Exiting showRelationalPreview as there are no Aggs defined");
      return;
    }
    XulDialog dialog = (XulDialog) document.getElementById(ELEM_ID_EXPORT_DIALOG);
    dialog.show();
  }

  public void closeDialog() {
    XulDialog dialog = (XulDialog) document.getElementById(ELEM_ID_EXPORT_DIALOG);
    dialog.hide();
    updateAggDetails();
  }

  private boolean overlayAdded = false;

  public void setConnected(boolean connected) throws XulException {
    final String OVERLAY = "org/pentaho/aggdes/ui/resources/exportDialogOverlay.xul"; //$NON-NLS-1$
    if (!connected) {
      document.removeOverlay(OVERLAY);
      overlayAdded = false;
      return;
    }

    boolean addOverlay = false;
    Class[] classes = outputService.getSupportedArtifactGeneratorClasses();
    for (Class clazz : classes) {
      if (PopulateScriptGenerator.class.isAssignableFrom(clazz)) {
        // just check for DML and assume that schema generator is there too
        addOverlay = true;
      }
    }

    if (addOverlay) {
      // don't add more than once
      if (!overlayAdded) {
        document.addOverlay(OVERLAY);
        overlayAdded = true;
      }
    }
  }

  public void showPreview() throws XulException {
    XulDialog previewDialog = (XulDialog) document.getElementById(ELEM_ID_PREVIEW_DIALOG);

    StringBuilder ddlBuf = new StringBuilder();
    List<UIAggregate> aggList = getEnabledAggs();
    XulTextbox ddlField = (XulTextbox) document.getElementById(ELEM_ID_DDL_FIELD);

    List<Output> outputs = new ArrayList<Output>();
    for (UIAggregate agg : aggList) {
      outputs.add(agg.getOutput());
    }

    if (outputs.size() > 0) {
      try {
        ddlBuf.append(outputService.getFullArtifact(outputs, CreateScriptGenerator.class));
      } catch (OutputValidationException e) {
        e.printStackTrace();
        // TODO mlowery show an error dialog before returning
        return;
      }

      ddlField.setValue(ddlBuf.toString());
    } else {
      ddlField.setValue(EMPTY_STRING);
    }

    StringBuilder dmlBuf = new StringBuilder();
    XulTextbox dmlField = (XulTextbox) document.getElementById(ELEM_ID_DML_FIELD);
    if (document.getElementById("dml_tabpanel") != null) {

      try {
        dmlBuf.append(outputService.getFullArtifact(outputs, PopulateScriptGenerator.class));
      } catch (OutputValidationException e) {
        e.printStackTrace();
        // TODO mlowery show an error dialog before returning
        return;
      }

      dmlField.setValue(dmlBuf.toString());
    }

    StringBuilder schemaBuf = new StringBuilder();
    XulTextbox multiDimSchemaField = (XulTextbox) document.getElementById("multiDimSchemaField");

    if (multiDimSchemaField != null) {
      outputs = new ArrayList<Output>();
      for (UIAggregate agg : aggList) {
        outputs.add(agg.getOutput());
      }

      try {
        schemaBuf.append(outputService.getFullArtifact(outputs, SchemaGenerator.class));
      } catch (OutputValidationException e) {
        e.printStackTrace();
        return;
      }

      multiDimSchemaField.setValue(schemaBuf.toString());
    }
    previewDialog.show();
  }

  public void hideRelationalPreview() {
    XulDialog previewDialog = (XulDialog) document.getElementById(ELEM_ID_PREVIEW_DIALOG);
    previewDialog.hide();
  }

  public void copyDdlToClipboard() {
    try {
      XulTextbox ddlField = (XulTextbox) document.getElementById(ELEM_ID_DDL_FIELD);
      Assert.notNull(ddlField, "could not find element with id '" + ELEM_ID_DDL_FIELD + "'");
      ((XulWindow) document.getRootElement()).copy(ddlField.getValue());

    } catch (XulException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
    }
  }

  public void copyDmlToClipboard() {

    try {
      XulTextbox dmlField = (XulTextbox) document.getElementById(ELEM_ID_DML_FIELD);
      Assert.notNull(dmlField, "could not find element with id '" + ELEM_ID_DML_FIELD + "'");
      ((XulWindow) document.getRootElement()).copy(dmlField.getValue());

    } catch (XulException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
    }
  }

  public void copyToClipboardMultiDimPreview() {
    XulTextbox multiDimSchemaField = (XulTextbox) document.getElementById("multiDimSchemaField");
    try {
      ((XulWindow) document.getRootElement()).copy(multiDimSchemaField.getValue());
    } catch (XulException e) {
      if (logger.isErrorEnabled()) {
        logger.error("an exception occurred", e);
      }
    }
  }

  public void startExecuteDdl() {
    if (logger.isDebugEnabled()) {
      logger.debug("enter startExecuteDdlDml");
    }

    XulDialog dialog = (XulDialog) document.getElementById(ELEM_ID_EXEC_PROGRESS_DIALOG);
    Assert.notNull(dialog, "could not find element with id '" + ELEM_ID_EXEC_PROGRESS_DIALOG + "'");

    final ExecutorCallback cb = new ExecutorCallback() {
      public void executionComplete(Exception e) {
        ExportHandler.this.done(e);
        if (ddlExecCallbackService != null && ddlExecCallbackService.getDdlCallbacks() != null) {
            for (DDLExecutionCompleteCallback callback : ddlExecCallbackService.getDdlCallbacks()) {
                callback.executionComplete(getEnabledAggs(), e);
            }
        }
      }
    };

    new Thread() {

      @Override
      public void run() {
        if (logger.isDebugEnabled()) {
          logger.debug("enter run");
        }

        List<String> sqls = ExportHandler.this.getOutput(true, false);

        ExportHandler.this.ddlDmlExecutor.execute(sqls.toArray(new String[0]), cb);

        if (logger.isDebugEnabled()) {
          logger.debug("exit run");
        }

      }

    }.start();

    dialog.show();
    if (logger.isDebugEnabled()) {
      logger.debug("exit startExecuteDdlDml");
    }
  }
  
  private void updateAggDetails() {
    if(aggController != null && aggList != null) {
      if(aggList.getSelectedIndex() > -1) {
        aggController.applyUiExtensions(aggList.getAgg(aggList.getSelectedIndex()));
      }
    }
  }

  public void startExecuteDml() {
    if (logger.isDebugEnabled()) {
      logger.debug("enter startExecuteDdlDml");
    }

    XulDialog dialog = (XulDialog) document.getElementById(ELEM_ID_EXEC_PROGRESS_DIALOG);
    Assert.notNull(dialog, "could not find element with id '" + ELEM_ID_EXEC_PROGRESS_DIALOG + "'");

    final ExecutorCallback cb = new ExecutorCallback() {
      public void executionComplete(Exception e) {
        ExportHandler.this.done(e);
      }
    };

    new Thread() {

      @Override
      public void run() {
        if (logger.isDebugEnabled()) {
          logger.debug("enter run");
        }

        List<String> sqls = ExportHandler.this.getOutput(false, true);

        ExportHandler.this.ddlDmlExecutor.execute(sqls.toArray(new String[0]), cb);

        if (logger.isDebugEnabled()) {
          logger.debug("exit run");
        }

      }

    }.start();

    dialog.show();
    if (logger.isDebugEnabled()) {
      logger.debug("exit startExecuteDdlDml");
    }
  }

  protected List<String> getOutput(boolean exportDdl, boolean exportDml) {

    List<UIAggregate> aggList = getEnabledAggs();
    List<String> sqls = new ArrayList<String>();

    for (UIAggregate agg : aggList) {
      Output output = agg.getOutput();

      if (exportDdl) {
        try {
          sqls.add(outputService.getArtifact(output, CreateScriptGenerator.class));
        } catch (OutputValidationException e) {
          e.printStackTrace();
          return Collections.emptyList();
        }
      }
    }

    for (UIAggregate agg : aggList) {
      Output output = agg.getOutput();

      if (exportDml) {
        try {
          sqls.add(outputService.getArtifact(output, PopulateScriptGenerator.class) + "\n\n"); //$NON-NLS-1$
        } catch (OutputValidationException e) {
          e.printStackTrace();
          return Collections.emptyList();
        }
      }

    }
    return sqls;
  }

  public void done(Exception e) {
    if (logger.isDebugEnabled()) {
      logger.debug("enter executeDdlDmlDone");
    }
    XulDialog dialog = (XulDialog) document.getElementById(ELEM_ID_EXEC_PROGRESS_DIALOG);
    Assert.notNull(dialog, "could not find element with id '" + ELEM_ID_EXEC_PROGRESS_DIALOG + "'");

    dialog.hide();

    if (null != e) {
      XulMessageBox msgBox;
      try {
        msgBox = (XulMessageBox) document.createElement(ELEM_ID_MESSAGEBOX);
      } catch (XulException e1) {
        logger.error("an exception occurred", e1);
        return;
      }
      msgBox.setMessage(e.getLocalizedMessage());
      msgBox.setTitle(Messages.getString("ExportHandler.DdlDmlExecutionErrorDialogTitle"));
      msgBox.setScrollable(true);
      msgBox.setHeight(300);
      msgBox.setWidth(400);
      msgBox.open();
    }

    if (logger.isDebugEnabled()) {
      logger.debug("exit executeDdlDmlDone");
    }
  }

  public void saveDdl() throws XulException {
    XulFileDialog fc = (XulFileDialog) document.createElement(ELEM_ID_FILEDIALOG);
    fc.setModalParent(((XulDialog)document.getElementById(ELEM_ID_EXPORT_DIALOG)).getRootObject());
    
    RETURN_CODE retVal = fc.showSaveDialog();
    File selectedFile = null;
    if (retVal == RETURN_CODE.OK) {
      selectedFile = (File) fc.getFile();
      if (logger.isDebugEnabled()) {
        logger.debug("Selected Save file: " + selectedFile.getAbsolutePath());
      }

      StringBuilder data = new StringBuilder();
      List<String> sqls = getOutput(true, false);
      for (String sql : sqls) {
        data.append(sql).append(System.getProperty(SYS_PROP_LINE_SEPARATOR));
      }
      try {
        FileUtils.writeStringToFile(selectedFile, data.toString());
      } catch (IOException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
      }

    }
  }

  public void saveDml() throws XulException {
    XulFileDialog fc = (XulFileDialog) document.createElement(ELEM_ID_FILEDIALOG);
    fc.setModalParent(((XulDialog)document.getElementById(ELEM_ID_EXPORT_DIALOG)).getRootObject());

    RETURN_CODE retVal = fc.showSaveDialog();
    File selectedFile = null;
    if (retVal == RETURN_CODE.OK) {
      selectedFile = (File) fc.getFile();
      if (logger.isDebugEnabled()) {
        logger.debug("Selected Save file: " + selectedFile.getAbsolutePath());
      }

      StringBuilder data = new StringBuilder();
      List<String> sqls = getOutput(false, true);
      for (String sql : sqls) {
        data.append(sql).append(System.getProperty(SYS_PROP_LINE_SEPARATOR));
      }
      try {
        FileUtils.writeStringToFile(selectedFile, data.toString());
      } catch (IOException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
      }

    }
  }

  public File saveOlap() throws XulException {

    // If we're not dealing with a MondrianFileSchemaModel object, something 
    // has gone wrong with the UI application state.
    if (!(connectionModel.getSelectedSchemaModel() instanceof MondrianFileSchemaModel)) {
      XulMessageBox msgBox = (XulMessageBox) document.createElement(ELEM_ID_MESSAGEBOX);
      msgBox.setMessage("Inconsistent application state: Only MondrianFileSchemaModel should call into this method");
      msgBox.open();
      logger.error("Inconsistent application state: Only MondrianFileSchemaModel should call into this method");
      return null;
    }
    
    XulFileDialog fc = (XulFileDialog) document.createElement(ELEM_ID_FILEDIALOG);
    fc.setModalParent(((XulDialog)document.getElementById(ELEM_ID_EXPORT_DIALOG)).getRootObject());


    MondrianFileSchemaModel schemaModel = (MondrianFileSchemaModel) connectionModel.getSelectedSchemaModel();

    RETURN_CODE retVal = fc.showSaveDialog(new File(schemaModel.getMondrianSchemaFilename()));

    File selectedFile = null;
    if (retVal == RETURN_CODE.OK) {
      selectedFile = (File) fc.getFile();
      if (logger.isDebugEnabled()) {
        logger.debug("Selected Save file: " + selectedFile.getAbsolutePath());
      }

      StringBuilder data = new StringBuilder();

      List<UIAggregate> aggList = getEnabledAggs();
      List<Output> outputs = new ArrayList<Output>();
      for (UIAggregate agg : aggList) {
        outputs.add(agg.getOutput());
      }

      try {
        data.append(outputService.getFullArtifact(outputs, SchemaGenerator.class));
      } catch (OutputValidationException e1) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e1);
        }
        return null;
      }

      try {
        FileUtils.writeStringToFile(selectedFile, data.toString());
        schemaModel.setMondrianSchemaFilename(selectedFile.getCanonicalPath());
        connectionModel.setSchemaUpToDate(true);
        return selectedFile;
      } catch (IOException e) {
        if (logger.isErrorEnabled()) {
          logger.error("an exception occurred", e);
        }
      }
    }
    return null;
  }

  @Required
  public void setConnectionModel(ConnectionModel connectionModel) {
    this.connectionModel = connectionModel;
  }

  public ConnectionModel getConnectionModel() {
    return connectionModel;
  }

  public AggList getAggList() {
    return aggList;
  }
  
  public List<UIAggregate> getEnabledAggs() {
    List<UIAggregate> enabledAggs = new ArrayList<UIAggregate>();
    for (UIAggregate agg : getAggList()) {
      if (agg.getEnabled()) {
        enabledAggs.add(agg);
      }
    }
    return enabledAggs;
  }

  public void setAggList(AggList aggList) {
    this.aggList = aggList;
  }
  
  @Required
  public void setAggController(AggController aggController) {
      this.aggController = aggController;
    }

  public Workspace getWorkspace() {
    return workspace;
  }

  public void setWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }

}
