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

import java.util.Map;

/**
 * This interface provides and extension point for eliciting inputs to your aggregate-generation algorithm from
 * a user.  A XUL overlay file is used to provide the form elements (textboxes, etc) and the Pentaho Aggregation
 * Designer will extract these parameters via the {@link #getAlgorithmParameters()} method.
 * 
 * @author APhillips
 * @see <a href="https://pentaho-community.atlassian.net/wiki/>Aggregation Designer Plugin Development</a>
 */
public interface AlgorithmUiExtension extends UiExtension {
  /**
   * Get a key-value map of algorithm parameters from your implementation of AlgorithmUiExtension.
   * @return a key-value map of algorithm parameters
   */
  public Map<String, String> getAlgorithmParameters();
}
