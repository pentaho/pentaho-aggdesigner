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

package org.pentaho.aggdes.ui.ext.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.aggdes.ui.ext.AbstractUiExtension;
import org.pentaho.aggdes.ui.ext.AlgorithmUiExtension;

public class AdaptiveMonteCarloAlgorithmUIPlugin extends AbstractUiExtension implements
    AlgorithmUiExtension {

  private String maxAggregates, maxTime, maxDiskSpace, maxLoadTime;

  public void onLoad() {
    bind(this, "maxAggregates", "maxAggregatesTextbox", "value");
    bind(this, "maxTime", "maxTimeTextbox", "value");
//    bind(this, "maxDiskSpace", "diskSpaceTextbox", "value");
//    bind(this, "maxLoadTime", "maxLoadTimeTextbox", "value");
  }

  public String getOverlayPath() {
    return "org/pentaho/aggdes/ui/resources/adaptiveMonteCarloOverlay.xul";
  }

  public Map<String, String> getAlgorithmParameters() {
    Map<String, String> algorithmRawParams = new HashMap<String, String>();

    if (!StringUtils.isEmpty(maxAggregates)) {
      algorithmRawParams.put("aggregateLimit", maxAggregates);
    } 
    if (!StringUtils.isEmpty(maxTime)) {
      algorithmRawParams.put("timeLimitSeconds", maxTime);
    }
    if (!StringUtils.isEmpty(maxDiskSpace)) {
      algorithmRawParams.put("maxDiskSpace", maxDiskSpace);
    }
    if (!StringUtils.isEmpty(maxLoadTime)) {
          algorithmRawParams.put("maxLoadTime", maxLoadTime);
    }
    return algorithmRawParams;
  }
  
  /*
   * Model getters/setters
   */

  public String getMaxAggregates() {
    return maxAggregates;
  }

  public void setMaxAggregates(String maxAggregates) {
    this.maxAggregates = maxAggregates;
  }

  public String getMaxTime() {
    return maxTime;
  }

  public void setMaxTime(String maxTime) {
    this.maxTime = maxTime;
  }

  public String getMaxDiskSpace() {
    return maxDiskSpace;
  }

  public void setMaxDiskSpace(String maxDiskSpace) {
    this.maxDiskSpace = maxDiskSpace;
  }

  public String getMaxLoadTime() {
    return maxLoadTime;
  }

  public void setMaxLoadTime(String maxLoadTime) {
    this.maxLoadTime = maxLoadTime;
  }

  @Override
  public void onUnload() {
    
  }
}
