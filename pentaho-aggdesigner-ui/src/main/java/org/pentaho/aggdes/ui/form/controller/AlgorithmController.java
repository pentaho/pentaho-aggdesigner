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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.algorithm.Result;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils.ValidationException;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.ui.AlgorithmRunner;
import org.pentaho.aggdes.ui.AlgorithmRunner.Callback;
import org.pentaho.aggdes.ui.ext.AlgorithmUiExtension;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.AggregateNamingService;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;

@Controller
public class AlgorithmController extends AbstractXulEventHandler {

  private static final Log logger = LogFactory.getLog(AlgorithmController.class);

  private AlgorithmRunner algorithmRunner;

  private OutputService outputService;

  private AlgorithmUiExtension algorithmUiExtension;
  
  private AggregateNamingService aggNamingService;
  
  private ConnectionModel connectionModel;
  
  private AggList aggList;
  
  private XulDialog dialog;
  
  public void setAlgorithmRunner(AlgorithmRunner algorithmRunner) {
    this.algorithmRunner = algorithmRunner;
  }

  public void setOutputService(OutputService outputService) {
    this.outputService = outputService;
  }

  public void setAlgorithmUiExtension(AlgorithmUiExtension algorithmUiExtension) {
    this.algorithmUiExtension = algorithmUiExtension;
  }
  
  public void setAggregateNamingService(AggregateNamingService aggNamingService) {
    this.aggNamingService = aggNamingService;
  }
  

  public void onLoad() throws XulException {
    if (algorithmUiExtension != null) {
      logger.info("Adding algorithm overlay: "+algorithmUiExtension);
      document.addOverlay(algorithmUiExtension.getOverlayPath());
      algorithmUiExtension.onLoad();
    }
  }
  
  public void openDialog(){
    dialog = (XulDialog) document.getElementById("algorithm_dialog");
    dialog.show();
  }

  public void closeDialog(){
    dialog.hide();
  }

  public void startAlgo() throws XulException {
    try {
      XulDialog dialog = (XulDialog) document.getElementById("progressDialog");
      PrintWriter pw = new PrintWriter(System.out);
      try {
        // hard coded params for now
        algorithmRunner.start(algorithmUiExtension.getAlgorithmParameters(), new Callback() {
          public void algorithmDone() {
            algoDone();
          }
        });
      } finally {
        pw.flush();
      }
      dialog.show();
      
      Result result = algorithmRunner.getResult();
      logger.debug("result=" + result);
  
      if (result == null || result.getAggregates() == null || result.getAggregates().size() == 0) {
        XulMessageBox box = (XulMessageBox) document.createElement("messagebox");
        box.setTitle(Messages.getString("AlgorithmController.NoResultsTitle"));
        box.setMessage(Messages.getString("AlgorithmController.NoResultsMessage"));
        box.open();
        return;
      }
      
      List<UIAggregate> algorithmAggs = new ArrayList<UIAggregate>();
      
      for (Aggregate agg : result.getAggregates()) {
          UIAggregateImpl cagg = new UIAggregateImpl();
          cagg.setName(agg.getCandidateTableName());
          cagg.setAlgoAgg(true);
          cagg.setDescription(agg.getDescription());
          cagg.setEstimateRowCount(agg.estimateRowCount());
          cagg.setEstimateSpace(agg.estimateSpace());
          
          List<Attribute> list = new ArrayList<Attribute>(agg.getAttributes());
          cagg.setAttributes(list);
  
          List<Measure> measureList = new ArrayList<Measure>(agg.getMeasures());
          cagg.setMeasures(measureList);
  
          algorithmAggs.add(cagg);
      }
      
      // reverse the collection, the results from the algorithm seem to
      // come back in reverse order of benefit
      
      Collections.reverse(algorithmAggs);
      
      // rename the aggregates appropriately
      aggNamingService.nameAggregates(algorithmAggs, getAggList(), connectionModel.getSchema());
      
      // add all the uiaggs to the agg list
      for (UIAggregate agg : algorithmAggs) {
        try {
          agg.setOutput(outputService.generateDefaultOutput(agg));
        } catch (OutputValidationException e) {
          System.err.println("Failed to create output, skipping UIAggregate");
          e.printStackTrace();
        }
  //      getAggList().addAgg(agg);
      }
      getAggList().addAggs(algorithmAggs);
      
      closeDialog();
    } catch (ValidationException validationException) {
      logger.error("error", validationException);
      XulMessageBox box = (XulMessageBox) document.createElement("messagebox");
      box.setTitle(Messages.getString("AlgorithmController.ErrorTitle"));
      box.setMessage(Messages.getString("AlgorithmController.ValidationErrorMessage", validationException.getMessage()));
      box.open();
    } catch (Throwable throwable) {
      logger.error("error", throwable);
      XulMessageBox box = (XulMessageBox) document.createElement("messagebox");
      box.setTitle(Messages.getString("AlgorithmController.ErrorTitle"));
      box.setMessage(Messages.getString("AlgorithmController.GeneralErrorMessage"));
      box.open();
    }
  }

  public void stopAlgo() {
    logger.debug("enter stopAlgo");
    XulDialog dialog = (XulDialog) document.getElementById("progressDialog");
    dialog.hide();
    algorithmRunner.stop();
  }

  private void algoDone() {
    logger.debug("enter algoDone");
    XulDialog dialog = (XulDialog) document.getElementById("progressDialog");
    dialog.hide();
    
  }

  public AggList getAggList() {
  
    return aggList;
  }

  public void setAggList(AggList aggList) {
  
    this.aggList = aggList;
  }

  public ConnectionModel getConnectionModel() {
  
    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {
  
    this.connectionModel = connectionModel;
  }
}
