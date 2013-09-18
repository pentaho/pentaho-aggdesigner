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

package org.pentaho.aggdes.ui;

import java.util.Properties;

/**
 * Optional configuration bean for the Aggregate Designer. If an instance is present in plugins.xml, it will 
 * apply the specified look and feel and resource bundle.
 * 
 * 
 * @author nbaker
 */
public class UIConfiguration {

  private String lookAndFeel;
  private String resourceBundle;
    
  public UIConfiguration(){
    
  }

  /**
   * Gets the specified look and feel
   * @return look and feel
   */
  public String getLookAndFeel() {
  
    return lookAndFeel;
  }

  /**
   * Accepts "system", "metal" or a fully qualified class name
   * 
   * @param lookAndFeel String
   */
  public void setLookAndFeel(String lookAndFeel) {
  
    this.lookAndFeel = lookAndFeel;
  }

  /**
   * Returns the base name of the user-specified resource bundle.
   * 
   * @return
   */
  public String getResourceBundle() {
  
    return resourceBundle;
  }

  
  /**
   * Set the base name for the resource bundle Aggreategate Designer will use at startup.
   * 
   * @param resourceBundle
   */
  public void setResourceBundle(String resourceBundle) {
  
    this.resourceBundle = resourceBundle;
  }
  
  
}

  
