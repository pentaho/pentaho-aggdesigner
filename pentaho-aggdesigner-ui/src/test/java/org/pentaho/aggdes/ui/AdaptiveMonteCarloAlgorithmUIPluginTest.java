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


package org.pentaho.aggdes.ui;

import java.util.Map;

import junit.framework.TestCase;

import org.pentaho.aggdes.ui.ext.impl.AdaptiveMonteCarloAlgorithmUIPlugin;
import org.pentaho.ui.xul.XulEventSource;

public class AdaptiveMonteCarloAlgorithmUIPluginTest extends TestCase {
  
  int bind = 0;
  
  public void test() throws Exception {
    
    AdaptiveMonteCarloAlgorithmUIPlugin plugin = new AdaptiveMonteCarloAlgorithmUIPlugin() {
      public void bind(XulEventSource model, String modelPropertyName, String xulComponentElementId, String xulComponentPropertyName){
        bind++;
      }
    };
    
    plugin.onLoad();
    
    // only bind two params for now
    assertEquals(bind, 2);
    
    // does nothing at the moment
    plugin.onUnload();
    
    assertEquals(bind, 2);
    
    assertEquals(plugin.getOverlayPath(), "org/pentaho/aggdes/ui/resources/adaptiveMonteCarloOverlay.xul");
    
    Map<String,String> params = plugin.getAlgorithmParameters();
    
    assertEquals(params.size(), 0);
    
    plugin.setMaxAggregates("1");
    assertEquals("1", plugin.getMaxAggregates());
    
    plugin.setMaxDiskSpace("2");
    assertEquals("2", plugin.getMaxDiskSpace());
    
    plugin.setMaxLoadTime("3");
    assertEquals("3", plugin.getMaxLoadTime());
    
    plugin.setMaxTime("4");
    assertEquals("4", plugin.getMaxTime());
    
    params = plugin.getAlgorithmParameters();
    
    assertEquals(params.size(), 4);
    assertEquals(params.get("aggregateLimit"), "1");
    assertEquals(params.get("maxDiskSpace"), "2");
    assertEquals(params.get("maxLoadTime"), "3");
    assertEquals(params.get("timeLimitSeconds"), "4");
    
    
  }
}
