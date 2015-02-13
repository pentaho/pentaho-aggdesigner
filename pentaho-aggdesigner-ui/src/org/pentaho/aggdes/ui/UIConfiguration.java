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

package org.pentaho.aggdes.ui;

import java.util.Properties;

/**
 * Optional configuration bean for the Aggregate Designer. If an
 * instance is present in plugins.xml, it will apply the specified
 * look and feel and resource bundle.
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


