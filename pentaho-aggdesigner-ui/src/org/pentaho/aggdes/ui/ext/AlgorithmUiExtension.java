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
package org.pentaho.aggdes.ui.ext;

import java.util.Map;

/**
 * This interface provides and extension point for eliciting inputs to your aggregate-generation algorithm from
 * a user.  A XUL overlay file is used to provide the form elements (textboxes, etc) and the Pentaho Aggregation
 * Designer will extract these parameters via the {@link #getAlgorithmParameters()} method.
 * 
 * @author APhillips
 * @see <a href="http://wiki.pentaho.com/display/Surfboard/03.+Aggregation+Designer+Plugin+Development">Aggregation Designer Plugin Development</a>
 */
public interface AlgorithmUiExtension extends UiExtension {
  /**
   * Get a key-value map of algorithm parameters from your implementation of AlgorithmUiExtension.
   * @return a key-value map of algorithm parameters
   */
  public Map<String, String> getAlgorithmParameters();
}