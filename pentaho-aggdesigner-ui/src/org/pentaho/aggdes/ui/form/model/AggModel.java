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
package org.pentaho.aggdes.ui.form.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.algorithm.Algorithm;
import org.pentaho.aggdes.model.Aggregate;
import org.pentaho.aggdes.model.Attribute;
import org.pentaho.aggdes.model.Dimension;
import org.pentaho.aggdes.model.Level;
import org.pentaho.aggdes.model.Measure;
import org.pentaho.aggdes.ui.ext.OutputUiExtension;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.stereotype.FormModel;

@FormModel
public class AggModel extends XulEventSourceAdapter {

  private UIAggregate thinAgg = new UIAggregateImpl();
  
  private OutputUiExtension currentUiExtension = null;

  private List<DimensionRowModel> dimensionRowModels = new ArrayList<DimensionRowModel>();

  private List<? extends Level> levels;

  private static final Log logger = LogFactory.getLog(AggModel.class);

  private String name, desc;

  private boolean modified;

  private Algorithm algorithm;

  private ConnectionModel connectionModel;

  public void setAlgorithm(Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  public void setCurrentUiExtension(OutputUiExtension currentUiExtension) {
    this.currentUiExtension = currentUiExtension;
  }

  /*
   * Returns whether the aggregate editor form has been modified.  This includes default
   * aggregate properties as well as UI extension form properties.
   */
  public boolean isModified() {
    return modified || (currentUiExtension != null && currentUiExtension.isModified());
  }

  public void setModified(boolean modified) {
    this.modified = modified;
    firePropertyChange("modified", null, modified);
  }

  public UIAggregate getThinAgg() {
    return thinAgg;
  }

  public void setThinAgg(UIAggregate thinAgg) {

    // if thinAgg is null, clear form.
    if (thinAgg == null) {
      clearForm();
      return;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("setThinAgg(" + thinAgg + ")");
      logger.debug("       agg name: " + thinAgg.getName());
      logger.debug("agg description: " + thinAgg.getName());
      logger.debug("       agg type: "
          + (thinAgg.isAlgoAgg() ? Messages.getString("agg_type_advisor") : Messages.getString("agg_type_custom")));
      logger.debug("     attributes:");
      for (Attribute attrib : thinAgg.getAttributes()) {
        logger.debug("                " + attrib.getLabel());
      }
    }

    ArrayList<DimensionRowModel> newDimensionRowModels = new ArrayList<DimensionRowModel>();
    this.thinAgg = thinAgg;
    setName(thinAgg.getName());
    setDesc(thinAgg.getDescription());

    //ensure that these get fired by sending null as previous
    firePropertyChange("name", null, name);
    firePropertyChange("desc", null, desc);

    if (connectionModel.getSchema() != null) {
      for (Dimension dim : connectionModel.getSchema().getDimensions()) {

        DimensionRowModel rowModel = new DimensionRowModel();
        rowModel.setDimension(dim);
        newDimensionRowModels.add(rowModel);
        
        //setup listener to mark model dirty when a user changes the level selection
        rowModel.addPropertyChangeListener("selectedIndex", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            setModified(true);
          }
        });

        rowModel.initSelected(thinAgg.getAttributes());
      }
      logger.debug("calling setDimensionRowModels with " + newDimensionRowModels);
      setDimensionRowModels(newDimensionRowModels);
    }
    setModified(false);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Object oldVal = this.name;
    this.name = name;
    setModified(true);
    firePropertyChange("name", oldVal, name);
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    Object oldVal = this.desc;
    this.desc = desc;
    setModified(true);
    firePropertyChange("desc", oldVal, desc);
  }

  public List<DimensionRowModel> getDimensionRowModels() {
    return dimensionRowModels;
  }

  public void setDimensionRowModels(List<DimensionRowModel> dimensionRowModels) {
    this.dimensionRowModels = dimensionRowModels;
    setModified(true);
    firePropertyChange("dimensionRowModels", null, dimensionRowModels);
  }

  public void clearForm() {
    logger.debug("clearForm()");
    setName("");
    setDesc("");
    setDimensionRowModels(new ArrayList<DimensionRowModel>());
    //setThinAgg(new UIAggregateImpl());
    setModified(false);
  }
  
  public void reset() {
    setThinAgg(thinAgg);

    if (currentUiExtension != null) {
      currentUiExtension.loadOutput(thinAgg.getOutput());
    }
  }

  /**
   * Synchronizes selections in DimensionRowModels to the backing UIAggregate.  
   * This typically happens when the user clicks "Save" or "Apply".
   */
  public void synchToAgg() {
    thinAgg.setName(this.name);
    thinAgg.setDescription(this.desc);

    thinAgg.setAlgoAgg(false);

    List<Attribute> attributes = new ArrayList<Attribute>();

    for (DimensionRowModel row : dimensionRowModels) {
      Level level = row.getSelectedItem();
      logger.debug("selected item is " + level.getName());
      int insertPoint = attributes.size();
      while (level != null) {
        Attribute attrib = level.getAttribute();
        if (attrib != null) {
          logger.debug("adding level " + level.getName() + " to UIAggregate: " + thinAgg);
          if (!attributes.contains(attrib)) {
            attributes.add(insertPoint, attrib);
          }
        }
        level = level.getParent();
      }
    }
    thinAgg.setAttributes(attributes);

    // for now, hard code all measures as selected
    List<Measure> measures = new ArrayList<Measure>();
    measures.addAll(connectionModel.getSchema().getMeasures());
    thinAgg.setMeasures(measures);
    setModified(false);

    // resync algorithm calculations
    Aggregate algoAggregate = algorithm.createAggregate(connectionModel.getSchema(), thinAgg.getAttributes());
    thinAgg.setEstimateRowCount(algoAggregate.estimateRowCount());
    thinAgg.setEstimateSpace(algoAggregate.estimateSpace());

  }

  public ConnectionModel getConnectionModel() {

    return connectionModel;
  }

  public void setConnectionModel(ConnectionModel connectionModel) {

    this.connectionModel = connectionModel;
  }
}
