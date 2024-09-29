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

  
