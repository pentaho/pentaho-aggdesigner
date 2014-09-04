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
