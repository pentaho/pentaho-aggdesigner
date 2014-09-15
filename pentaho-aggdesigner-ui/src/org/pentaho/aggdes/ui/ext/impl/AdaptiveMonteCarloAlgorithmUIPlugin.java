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
