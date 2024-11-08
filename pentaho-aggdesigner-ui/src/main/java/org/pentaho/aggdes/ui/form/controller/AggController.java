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
* Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.ui.form.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.aggdes.output.Output;
import org.pentaho.aggdes.output.OutputService;
import org.pentaho.aggdes.output.OutputValidationException;
import org.pentaho.aggdes.ui.OutputUIService;
import org.pentaho.aggdes.ui.ext.OutputUiExtension;
import org.pentaho.aggdes.ui.form.model.AggModel;
import org.pentaho.aggdes.ui.model.AggList;
import org.pentaho.aggdes.ui.model.AggListEvent;
import org.pentaho.aggdes.ui.model.AggListListener;
import org.pentaho.aggdes.ui.model.UIAggregate;
import org.pentaho.aggdes.ui.model.AggListEvent.Type;
import org.pentaho.aggdes.ui.model.impl.UIAggregateImpl;
import org.pentaho.aggdes.ui.util.Messages;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Controller;
import org.pentaho.ui.xul.stereotype.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
@Controller
public class AggController extends AbstractXulEventHandler {

  /*
   * The OutputUiExtension acts as both a controller and a model.  The controller
   * behaviors are managed by this class while the model behaviors are encapsulated
   * in AggModel, the corresponding FormModel of the AggController.
   */
  private OutputUiExtension currentUiExtension = null;

  private OutputService outputService;

  private OutputUIService outputUIService;

  private AggModel aggModel;

  private boolean initialized = false;

  private AggList aggList;

  private BindingFactory bindingFactory;

  private static final Log logger = LogFactory.getLog(AggController.class);

  @Autowired
  public void setAggModel(AggModel aggModel) {
    this.aggModel = aggModel;
  }

  @Autowired
  public void setOutputUIService(OutputUIService outputUIService) {
    this.outputUIService = outputUIService;
  }

  @Autowired
  public void setOutputService(OutputService outputService) {
    this.outputService = outputService;
  }

  @Autowired
  public void setAggList(AggList aggList) {
    this.aggList = aggList;
  }

  @Autowired
  public void setBindingFactory(BindingFactory bindingFactory) {
    this.bindingFactory = bindingFactory;
  }

  @RequestHandler
  public void onLoad() {
    bindingFactory.setDocument(document);

    bindingFactory.createBinding(aggModel, "name", "aggregateName", "value");
    bindingFactory.createBinding(aggModel, "desc", "aggregateDescription", "value");
    bindingFactory.createBinding(aggModel, "dimensionRowModels", "dimtable", "elements");

    if (aggModel.getThinAgg() == null || !initialized) {
      removeUiExtensions();
    }

    BindingConvertor<int[], Boolean> selectedItemToBoolean = new BindingConvertor<int[], Boolean>() {
      public Boolean sourceToTarget(int[] value) {
        return !(value.length > 0);
      }

      //not used
      public int[] targetToSource(Boolean value) {
        return null;
      }
    };

    bindingFactory.setBindingType(Binding.Type.ONE_WAY);

    bindingFactory.createBinding("definedAggTable", "selectedRows", "aggregateName", "disabled", selectedItemToBoolean);
    bindingFactory.createBinding("definedAggTable", "selectedRows", "aggregateDescription", "disabled",
        selectedItemToBoolean);
    bindingFactory.createBinding("definedAggTable", "selectedRows", "agg_remove", "disabled", selectedItemToBoolean);
    bindingFactory.createBinding("definedAggTable", "selectedRows", "agg_up", "disabled", selectedItemToBoolean);
    bindingFactory.createBinding("definedAggTable", "selectedRows", "agg_down", "disabled", selectedItemToBoolean);

    BindingConvertor<UIAggregate, Boolean> aggModelChangedConvertor = new BindingConvertor<UIAggregate, Boolean>() {
      public Boolean sourceToTarget(UIAggregate value) {
        return (value == null);
      }

      public UIAggregate targetToSource(Boolean value) {
        return null;
      }
    };

    bindingFactory.createBinding(aggModel, "thinAgg", "apply_agg_btn", "disabled", aggModelChangedConvertor);
    bindingFactory.createBinding(aggModel, "thinAgg", "reset_agg_btn", "disabled", aggModelChangedConvertor);

    bindingFactory.createBinding(aggModel, "modified", "apply_agg_btn", "!disabled");
    bindingFactory.createBinding(aggModel, "modified", "reset_agg_btn", "!disabled");

    //Bind check-all, un-check-all buttons to model
    BindingConvertor<List, Boolean> convertor = new BindingConvertor<List, Boolean>() {
      public Boolean sourceToTarget(List value) {
        logger.debug("Set button disabled : " + (!(value != null && value.size() > 0)));
        return !(value != null && value.size() > 0);
      }

      //not used
      public List targetToSource(Boolean value) {
        return null;
      }
    };

    aggList.addAggListListener(new AggListListener() {

      public void listChanged(AggListEvent e) {
        if (e.getType() == Type.SELECTION_CHANGING) {
          //check for unsaved changes
          if (aggModel.isModified()) {
            promptSaveRequired();
          }
        }
        if (e.getType() == Type.SELECTION_CHANGED) {
          applyUiExtensions(AggController.this.aggList.getAgg(e.getIndex()));
        }
      }
    });

    aggModel.setThinAgg(new UIAggregateImpl());
    initialized = true;
  }

  /**
   * generate optional UI as required by Aggregate's output method
   * 
   * @param thinAgg UIAggregate that we're working on
   * @throws XulException 
   */
  public void applyUiExtensions(UIAggregate thinAgg) {

    if (thinAgg == null) {
      removeUiExtensions();
      return;
    }

    Output aggOutput = thinAgg.getOutput();
    OutputUiExtension uiExtension = outputUIService.getUiExtension(aggOutput);

    //reload an extension (xul overlay) if the target extension has changed
    if (uiExtension != currentUiExtension) {
      removeUiExtensions();
    }
    if (uiExtension == null) {
      return;
    }

    if (currentUiExtension != uiExtension) {
      try {
        document.addOverlay(uiExtension.getOverlayPath());
        uiExtension.onLoad();

        bindingFactory.setBindingType(Binding.Type.ONE_WAY);
        bindingFactory.createBinding(uiExtension, "modified", "apply_agg_btn", "!disabled");
        bindingFactory.createBinding(uiExtension, "modified", "reset_agg_btn", "!disabled");

        currentUiExtension = uiExtension;
        //aggModel needs a reference to the uiExtension so it can test for form modification
        aggModel.setCurrentUiExtension(uiExtension);
      } catch (Exception e) {
        logger.error("Error adding output UI extensions", e);
      }
    }

    currentUiExtension.loadOutput(aggOutput);
  }

  private void promptSaveRequired() {
    XulMessageBox msgBox;
    try {
      msgBox = (XulMessageBox) document.createElement("messagebox");
      msgBox.setTitle(Messages.getString("AggController.SaveOutputTitle"));
      String message = Messages.getString("AggController.SaveOutputMessage") + "\n"
          + Messages.getString("AggController.SaveOutputMessageSaveChanges");
      msgBox.setMessage(message);
      msgBox.setButtons(new String[] { Messages.getString("save"), Messages.getString("do_not_save") });

      int id = msgBox.open();
      if (id == 0) {
        apply();
      } else {
        reset();
      }
    } catch (XulException e) {
      logger.error("Could not launch save prompt message dialog", e);
    }
  }

  /**
   * Removed current additions to the UI made by a previous UIGenerator
   * Basically it reverses the overlay
   */
  private void removeUiExtensions() {
    if (currentUiExtension == null) {
      return;
    }

    try {
      document.removeOverlay(currentUiExtension.getOverlayPath());
    } catch (Exception e) {
      logger.error("Error removing output UI extensions", e);
    }
    currentUiExtension.onUnload();
    currentUiExtension = null;
    aggModel.setCurrentUiExtension(null);
  }

  @RequestHandler
  public void apply() {
    aggModel.synchToAgg();

    //need to regen the output object since some properties may have to be regenerated due to changes on the Agg
    try {
      aggModel.getThinAgg().setOutput(outputService.generateDefaultOutput(aggModel.getThinAgg()));
    } catch (OutputValidationException e) {
      logger.error("failed to generate output", e);
    }

    if (currentUiExtension != null) {
      currentUiExtension.saveOutputChanges(aggModel.getThinAgg().getOutput());
    }

    aggList.aggChanged(aggModel.getThinAgg());
  }

  @RequestHandler
  public void reset() {
    aggModel.reset();
  }
}
