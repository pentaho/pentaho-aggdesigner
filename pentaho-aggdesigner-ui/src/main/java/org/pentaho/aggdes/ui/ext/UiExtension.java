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

package org.pentaho.aggdes.ui.ext;

/**
 * This interface captures the root contract that all user interface extensions must follow.  User interface
 * extensions in Pentaho Aggregation Designer work by providing a XUL Overlay file via {@link #getOverlayPath()}.
 * The XUL framework merges the form elements you have defined in your overlay file into the application.
 * 
 * @author APhillips
 * @see <a href="https://pentaho-community.atlassian.net/wiki/spaces/ServerDoc2x/pages/1506181986/07.+Includes+and+Overlays">XUL Overlays</a>
 * @see <a href="https://pentaho-community.atlassian.net/wiki/spaces/ServerDoc2x/pages/1504674127/03.+XUL+1.0+Extensions+and+the+Pentaho+XUL+Namespace">Pentaho XUL Framework</a>
 * @see <a href="https://pentaho-community.atlassian.net/wiki/">Aggregation Designer Plugin Development</a>
 */
public interface UiExtension {

  /**
   * Trigger for your UiExtension to initialize itself.  This typically includes compiling references
   * to XulComponent objects and setting up XulComponet-to-model bindings.  This method is invoked once
   * when your extension is first required.
   */
  public void onLoad();
  
  /**
   * Optional method called when the UIExtension is unloaded from the current context. This is where 
   * an implementation should destroy any bindings created and release and resources.
   */
  void onUnload();

  /**
   * Your UiExtension must provide a path to a xul overlay source file (from classpath root).  This file should 
   * include an <overlay> tag that includes all XulElements to be overlayed.  The overlay is applied 
   * only once, when your extension is first required.  Following the application of the overlay to 
   * the XulDomContainer, the {@link #onLoad()} will be invoked.
   * @return path to a XUL overlay source file.
   */
  public String getOverlayPath();

}
