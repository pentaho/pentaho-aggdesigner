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
* Copyright 2006 - 2017 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.ui.ext;

import org.pentaho.aggdes.output.Output;

/**
 * Aggregate Output UI extensions allow plugin developers to extend the Pentaho Aggregation Designer user 
 * interface to support their custom Aggregate Output objects.  You will want to create an implementation
 * of {@link OutputUiExtension}, for instance, if you want to render data from (or edit) your custom 
 * {@link Output} in the user interface.
 * 
 * @author aphillips
 */
public interface OutputUiExtension extends UiExtension {

  /**
   * Returns whether your OutputUiExtension implementation can provide UI support for the given Output object
   * 
   * @param output instance of the Output that we want to support with a UI
   * @return true if this implementation can support this particular Output
   */
  public boolean accept(Output output);

  /**
   * A trigger for your extension implementation to apply changes made in the user interface to the Output
   * object.  The changes will be applied to the provided Output object.
   * This method is called when the Apply button is pressed.
   * @param output instance of the Output that we want to save changes to
   */
  public void saveOutputChanges(Output output);

  /**
   * Provides the Output object that your extension will modify.  {@link #loadOutput} is called 
   * whenever the user selected Aggregate changes.
   * @param output The aggregate output to be edited
   */
  public void loadOutput(Output output);

  /**
   * A flag that reflects the modified state of your form.  Once the user begins editing in your extension's UI,
   * you should fire a property change event for the "modified" property.  This will cause the Apply and Reset buttons
   * in the aggregate editor panel to become activated.
   * @see #saveOutputChanges()
   * @return flag reflecting the modified state of the form
   */
  public boolean isModified();
}
