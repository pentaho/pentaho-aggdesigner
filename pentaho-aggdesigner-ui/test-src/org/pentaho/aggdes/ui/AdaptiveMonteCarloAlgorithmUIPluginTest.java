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
