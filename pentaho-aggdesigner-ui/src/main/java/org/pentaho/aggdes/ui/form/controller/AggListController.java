/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.ui.form.controller;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.algorithm.util.ArgumentUtils;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Parameter;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.ui.Workspace;
import org.pentaho.aggdes.ui.ext.AlgorithmUiExtension;
import org.pentaho.aggdes.ui.form.model.AggModel;
import org.pentaho.aggdes.ui.form.model.AggregateSummaryModel;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.AggListEvent;
import org.pentaho.aggdes.ui.model.AggListListener;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.AggregateNamingService;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBinding;
import org.pentaho.ui.xul.components.XulButton;
import org.pentaho.ui.xul.components.XulImage;
import org.pentaho.ui.xul.components.XulTreeCell;
import org.pentaho.ui.xul.containers.XulTree;
import org.pentaho.ui.xul.containers.XulTreeRow;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

//FIXME: Use XUL data binding to remove all references to XulComponents

/**
 * AggListController manages the UI for the Aggregate Table and the Aggregate
 * Summary Screen, including the Cost / Benefit chart. 
 */
@Controller
public class AggListController extends AbstractXulEventHandler {
  
  private final DecimalFormat format = new DecimalFormat("#0");
  
  private static final Log logger = LogFactory.getLog(AggListController.class);

  @Deprecated
  private XulTree aggLevelTable;

  @Deprecated
  private XulTree aggTable;

  private OutputService outputService;

  private AggModel aggModel;

  private AggregateNamingService aggNamingService;
  
  private AggregateSummaryModel aggregateSummaryModel;
  
  private Algorithm algorithm;
  
  private AlgorithmUiExtension algorithmUiExtension;
  
  private AggList aggList;
  
  private Workspace workspace;
  
  private ConnectionModel connectionModel;

  private BindingFactory bindingFactory;

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  
  public AggList getAggList() {
    return aggList;
  }

  public void setAggList(AggList aggList) {
  
    this.aggList = aggList;
  }

  public void setAggModel(AggModel aggModel) {
    this.aggModel = aggModel;
  }

  public void setWorkspace(Workspace workspace) {
    this.workspace = workspace;
  }
  
  public void setOutputService(OutputService outputService) {
    this.outputService = outputService;
  }

  public void setAggregateNamingService(AggregateNamingService aggNamingService) {
    this.aggNamingService = aggNamingService;
  }
  
  public void setAggregateSummaryModel(AggregateSummaryModel aggregateSummaryModel) {
    this.aggregateSummaryModel = aggregateSummaryModel;
  }
  
  public void setAlgorithm(Algorithm algorithm) {
    this.algorithm = algorithm;
  }
  
  public void setAlgorithmUiExtension(AlgorithmUiExtension algorithmUiExtension) {
    this.algorithmUiExtension = algorithmUiExtension;
  }
  
  public void changeInAggregates() {
    
    // set dirty bit for workspace
    workspace.setWorkspaceUpToDate(false);
    
    // set the dirty bit for the schema publishing functionality
    
   if(getConnectionModel().getSchema() != null){
      getConnectionModel().setSchemaUpToDate(false);
    }
    
    // configure summary model and chart values
    
    int totalAggregatesSelected = 0;
    double totalRows = 0;
    double totalSpace = 0;
    double totalLoadTime = 0;

    //Fire event
    AggListController.this.firePropertyChange("aggList", null, aggList);
    
    List<Aggregate> algoAggregates = new ArrayList<Aggregate>();
    for (UIAggregate aggregate : getAggList()) {
      if (aggregate.getEnabled()) {
        totalAggregatesSelected++;
        totalRows += aggregate.estimateRowCount();
        totalSpace += aggregate.estimateSpace();
        algoAggregates.add(algorithm.createAggregate(connectionModel.getSchema(), aggregate.getAttributes()));
      }
    }

    double[] xs = new double[algoAggregates.size()];
    double[] startx = new double[algoAggregates.size()];
    double[] endx = new double[algoAggregates.size()];
    
    double[] ys = new double[algoAggregates.size()];
    double[] starty = new double[algoAggregates.size()];
    double[] endy = new double[algoAggregates.size()];

    XYSeries series1 = new XYSeries("CostBenefit");
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    DefaultIntervalXYDataset datasetxy = new DefaultIntervalXYDataset();
    
    if (connectionModel.getSchema() != null) {
    
      Map<Parameter, Object> algorithmParams = ArgumentUtils.validateParameters(algorithm, algorithmUiExtension.getAlgorithmParameters());
      List<Algorithm.CostBenefit> costBenefit = algorithm.computeAggregateCosts(connectionModel.getSchema(), algorithmParams, algoAggregates);

      double totalbenefit = 0;
      double x = 0;
      int count = 0;
      for (Algorithm.CostBenefit cb : costBenefit) {
        Aggregate agg = algoAggregates.get(count);
        double estimateSpace = agg.estimateSpace();
        double hx = estimateSpace / 2;
        totalLoadTime += cb.getLoadTime();
        totalbenefit += cb.getSavedQueryRowCount();
        series1.add(x + hx, totalbenefit);
        
        xs[count] = x + hx;
        startx[count] = x;
        x += estimateSpace;
        endx[count] = x;
  
        ys[count] = totalbenefit;
        starty[count] = 0;
        endy[count] = 0;
  
        count++;
      }
  
      // update summary table
      
      aggregateSummaryModel.setSelectedAggregateCount(format.format(totalAggregatesSelected));
      aggregateSummaryModel.setSelectedAggregateRows(format.format(totalRows));
      aggregateSummaryModel.setSelectedAggregateSpace(format.format(totalSpace) + " bytes");
      aggregateSummaryModel.setSelectedAggregateLoadTime(format.format(totalLoadTime));
    } else {
      aggregateSummaryModel.setSelectedAggregateCount("");
      aggregateSummaryModel.setSelectedAggregateRows("");
      aggregateSummaryModel.setSelectedAggregateSpace("");
      aggregateSummaryModel.setSelectedAggregateLoadTime("");
      
    }
    // render cost benefit chart
    
    double[][] data = new double[][] {xs, startx, endx, ys, starty, endy};
    datasetxy.addSeries("", data);
    
    JFreeChart chart = ChartFactory.createXYBarChart(
        "", // chart title
        "Cost", // x axis label
        false,
        "Benefit", // y axis label
        datasetxy, // data
        PlotOrientation.VERTICAL, // orientation
        false, // include legend
        false, // tooltips?
        false // URLs?
        );
    
    ((XYPlot)chart.getPlot()).getDomainAxis().setTickLabelsVisible(false);
    ((XYPlot)chart.getPlot()).getRangeAxis().setTickLabelsVisible(false);
    ((XYPlot)chart.getPlot()).setDataset(1, dataset);
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

    chart.setBackgroundPaint(
        new Color(Integer.parseInt(Messages.getString("chart_background_color").toUpperCase(), 16)));

    renderer.setSeriesPaint(0, 
        new Color(Integer.parseInt(Messages.getString("chart_line_color").toUpperCase(), 16)));
    
    ((XYPlot)chart.getPlot()).getRenderer(0).setSeriesPaint(0, 
        new Color(Integer.parseInt(Messages.getString("chart_bar_color").toUpperCase(), 16)));
    
    
    
    ((XYPlot)chart.getPlot()).setRenderer(1, renderer);
    ((XYPlot)chart.getPlot()).setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    XulImage image = (XulImage)document.getElementById("chart");
    image.setSrc(chart.createBufferedImage(309, 168));
  }
  
  public void onLoad() {
    bindingFactory.setDocument(document);
    bindingFactory.setBindingType(Binding.Type.BI_DIRECTIONAL);

    // bind summary model label details
    bindingFactory.createBinding(aggregateSummaryModel, "selectedAggregateCount", "num_aggs", "value");
    bindingFactory.createBinding(aggregateSummaryModel, "selectedAggregateRows", "num_rows", "value");
    bindingFactory.createBinding(aggregateSummaryModel, "selectedAggregateSpace", "disk_space", "value");
    
    
    //Bind check-all, un-check-all buttons to model
    BindingConvertor<AggList, Boolean> convertor = new BindingConvertor<AggList, Boolean>() {
      public Boolean sourceToTarget(AggList list) {
        return ! (list.getSize() > 0);
      }

      //not used
      public AggList targetToSource(Boolean value) {
        return null;
      }
    };

    bindingFactory.setBindingType(Binding.Type.ONE_WAY);
    
    bindingFactory.createBinding(this, "aggList", "agg_checkall", "disabled", convertor);
    
    bindingFactory.createBinding(this, "aggList", "agg_uncheckall", "disabled", convertor);
    

    convertor = new BindingConvertor<AggList, Boolean>() {
      public Boolean sourceToTarget(AggList list) {
        return (list.getSize() > 0);
      }

      //not used
      public AggList targetToSource(Boolean value) {
        return null;
      }
    };
    
    bindingFactory.createBinding(this, "aggList", connectionModel, "schemaLocked", convertor);
    
    
    // not displayed at this time, no unit of measure
    // bind(aggregateSummaryModel, "selectedAggregateLoadTime", "load_time", "value");

    
    aggLevelTable = (XulTree) document.getElementById("dimtable");
    aggTable = (XulTree) document.getElementById("definedAggTable");

    AggListListener aggListListener = new AggListListener() {

      public void listChanged(AggListEvent e) {
        final String ESTIMATE_UNKNOWN = "unknown";

        AggList list = (AggList) e.getSource();
        switch (e.getType()) {
        case AGGS_ADDED:
          try {
            for (int i = e.getIndex(); i < list.getSize(); i++) {
              UIAggregate newAgg = list.getAgg(i);
  
              XulTreeRow newRow = (XulTreeRow) document.createElement("treerow");
  
              XulTreeCell newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setValue(newAgg.getEnabled());
              newRow.addCell(newCell);
  
              newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setLabel(newAgg.isAlgoAgg() ? Messages.getString("agg_type_advisor") : Messages.getString("agg_type_custom"));
              newRow.addCell(newCell);
  
              newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setLabel(newAgg.getName());
              newRow.addCell(newCell);
  
              newCell = (XulTreeCell) document.createElement("treecell");
              double rowCount = newAgg.estimateRowCount();
              if (rowCount == 0) {
                newCell.setLabel(ESTIMATE_UNKNOWN);
              } else {
                newCell.setLabel(format.format(rowCount));
              }
              newRow.addCell(newCell);
  
              newCell = (XulTreeCell) document.createElement("treecell");
              double space = newAgg.estimateSpace();
              if (space == 0) {
                newCell.setLabel(ESTIMATE_UNKNOWN);
              } else {
                newCell.setLabel(format.format(space));
              }
              newRow.addCell(newCell);
  
              aggTable.addTreeRow(newRow);
            }
            changeInAggregates();
            
          } catch (XulException xe) {
            logger.error("Error adding new row to Agg table", xe);
          }
          break;
        
          case AGG_ADDED:
            try {
              UIAggregate newAgg = list.getAgg(e.getIndex());

              XulTreeRow newRow = (XulTreeRow) document.createElement("treerow");

              XulTreeCell newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setValue(newAgg.getEnabled());
              newRow.addCell(newCell);

              newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setLabel(newAgg.isAlgoAgg() ? Messages.getString("agg_type_advisor") : Messages.getString("agg_type_custom"));
              newRow.addCell(newCell);

              newCell = (XulTreeCell) document.createElement("treecell");
              newCell.setLabel(newAgg.getName());
              newRow.addCell(newCell);

              newCell = (XulTreeCell) document.createElement("treecell");
              double rowCount = newAgg.estimateRowCount();
              if (rowCount == 0) {
                newCell.setLabel(ESTIMATE_UNKNOWN);
              } else {
                newCell.setLabel(format.format(rowCount));
              }
              newRow.addCell(newCell);

              newCell = (XulTreeCell) document.createElement("treecell");
              double space = newAgg.estimateSpace();
              if (space == 0) {
                newCell.setLabel(ESTIMATE_UNKNOWN);
              } else {
                newCell.setLabel(format.format(space));
              }
              newRow.addCell(newCell);

              aggTable.addTreeRow(newRow);

              changeInAggregates();
              
            } catch (XulException xe) {
              logger.error("Error adding new row to Agg table", xe);
            }
            break;
          case AGGS_CLEARED:
            aggTable.getRootChildren().removeAll();
            aggModel.setThinAgg(null);
            changeInAggregates();
            break;
          case AGG_REMOVED:
            aggTable.removeTreeRows(new int[] { e.getIndex() });
            aggTable.clearSelection();
            int[] tableSelection = aggTable.getSelectedRows();
            if (e.getIndex() < list.getSize()) {
              //we're single selection
              list.setSelectedIndex(e.getIndex());
            }
            //There's aready a selection listener on the table firing the same.
            UIAggregate tempAgg = list.getSelectedValue();
            
            aggModel.setThinAgg(list.getSelectedValue());
            
            changeInAggregates();
                        
            break;
          case SELECTION_CHANGED:
            
            if (aggTable.getSelectedRows().length > 0) {
              logger.info("Event index: " + e.getIndex() + " aggTable.selectedRow0: " + aggTable.getSelectedRows()[0]);
            }
            int[] rows = aggTable.getSelectedRows();
            if (aggTable.getSelectedRows().length > 0 && e.getIndex() == aggTable.getSelectedRows()[0]) {
              aggModel.setThinAgg(list.getSelectedValue());
            }
            //check to see if it's already selected, ignore if so
            int[] curSelection = aggTable.getSelectedRows();
//            if(curSelection.length > 0 && e.getIndex() == curSelection[0]){
//              logger.debug("already selected row");
//              break;
//            }
            logger.debug("selecting new row");
            aggTable.setSelectedRows(new int[] { e.getIndex() });
            aggModel.setThinAgg(list.getSelectedValue());
            break;
          case AGG_CHANGED:
            logger.debug("agg list controller responding to AGG_CHANGED event from aggList");
            int idx = e.getIndex();
            UIAggregate agg = list.getAgg(idx);
            aggTable.getRootChildren().getItem(idx).getRow().getCell(0).setValue(agg.getEnabled());
            aggTable.getRootChildren().getItem(idx).getRow().getCell(1).setLabel(
                agg.isAlgoAgg() 
                ? Messages.getString("agg_type_advisor") 
                : Messages.getString("agg_type_custom")
            );
            aggTable.getRootChildren().getItem(idx).getRow().getCell(2).setLabel(agg.getName());

            double rowCount = agg.estimateRowCount();
            if (rowCount == 0) {
              aggTable.getRootChildren().getItem(idx).getRow().getCell(3).setLabel(ESTIMATE_UNKNOWN);
            } else {
              aggTable.getRootChildren().getItem(idx).getRow().getCell(3).setLabel(format.format(rowCount));
            }

            double space = agg.estimateSpace();
            if (space == 0) {
              aggTable.getRootChildren().getItem(idx).getRow().getCell(4).setLabel(ESTIMATE_UNKNOWN);
            } else {
              aggTable.getRootChildren().getItem(idx).getRow().getCell(4).setLabel(format.format(space));
            }
            
            // wrap the update in an invokeLater to avoid null pointer exceptions occurring deep in Swing code,
            // even though this method will normally be called within the event thread.
            document.invokeLater(new Runnable() {
            	public void run() {
            		aggTable.update();
            	}
            });
            changeInAggregates();
            
            break;
        }

      }

    };
    getAggList().addAggListListener(aggListListener);
    
    // what happens?
    changeInAggregates();
  }

  public void addAgg() {
    try {
      UIAggregate newAgg = new UIAggregateImpl();
      aggNamingService.nameAggregate(newAgg, getAggList(), connectionModel.getSchema());
      
      newAgg.setOutput(outputService.generateDefaultOutput(newAgg));
      
      getAggList().addAgg(newAgg);
      getAggList().setSelectedIndex(getAggList().getSize() - 1);
    } catch (OutputValidationException e) {
      System.err.println("Failed to create output, skipping UIAggregate");
      e.printStackTrace();
    }
  }

  public void showAgg(int idx) {
    aggLevelTable.clearSelection();
    Aggregate agg = getAggList().getAgg(idx);
    if (agg == null) {
      logger.info(String.format("List and Table out of sync, %s does not exist", idx));
    } else {
      getAggList().setSelectedIndex(idx);
    }
  }

  public void saveAggChange(int idx) {
    UIAggregate agg = getAggList().getAgg(idx);
    XulTree aggTable = (XulTree) document.getElementById("definedAggTable");
    XulTreeRow row = aggTable.getRootChildren().getItem(idx).getRow();
    agg.setEnabled((Boolean) row.getCell(0).getValue());

    // get row count estimate
    Aggregate algoAggregate = algorithm.createAggregate(connectionModel.getSchema(), agg.getAttributes());
    agg.setEstimateRowCount(algoAggregate.estimateRowCount());
    agg.setEstimateSpace(algoAggregate.estimateSpace());
    getAggList().aggChanged(agg);
    System.out.println("Saving agg, enabled? " + row.getCell(0).getValue());
    
  }

  public void displayNewOrExistingAgg() {
    if (getAggList().getSize() == 0) {
      addAgg();
    } else {
      if (getAggList().getSelectedIndex() == -1) {
        showAgg(0);
      } else {
        showAgg(getAggList().getSelectedIndex());
      }
    }
  }

  public void removeAgg() {
    if (aggTable == null) {
      return;
    }
    int[] selectedIndexes = aggTable.getSelectedRows();
    for (int pos : selectedIndexes) {
      //the user has chosen to delete this agg, so do not prompt to save any changes
      //just reset the form.
      aggModel.reset();
    	
      getAggList().removeAgg(pos);
    }

  }
  
  public void moveAggUp(){
    getAggList().moveAggUp(getAggList().getSelectedValue());
  }

  public void moveAggDown(){
    getAggList().moveAggDown(getAggList().getSelectedValue());
  }

  public void checkAll(){
    getAggList().checkAll();
  }

  public void uncheckAll(){
    getAggList().uncheckAll();
  }

  public ConnectionModel getConnectionModel() {
  
    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {
  
    this.connectionModel = connectionModel;
  }
}
