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
