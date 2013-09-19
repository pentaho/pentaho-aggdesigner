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

package org.pentaho.aggdes.ui.ext;

/**
 * This interface captures the root contract that all user interface extensions must follow.  User interface
 * extensions in Pentaho Aggregation Designer work by providing a XUL Overlay file via {@link #getOverlayPath()}.
 * The XUL framework merges the form elements you have defined in your overlay file into the application.
 * 
 * @author APhillips
 * @see <a href="http://wiki.pentaho.com/display/PLATFORM/07.+Includes+and+Overlays">XUL Overlays</a>
 * @see <a href="http://wiki.pentaho.com/display/PLATFORM/The+Pentaho+XUL+Framework+Developer%27s+Guide">Pentaho XUL Framework</a>
 * @see <a href="http://wiki.pentaho.com/display/Surfboard/03.+Aggregation+Designer+Plugin+Development">Aggregation Designer Plugin Development</a>
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
