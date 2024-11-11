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


package org.pentaho.aggdes.ui.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
  private static final String BUNDLE_NAME = "org.pentaho.aggdes.ui.resources.mainFrame"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  private Messages() {
  }

  public static String getString(String key, Object... params) {
    try {
      return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
    } catch (Exception e) {
      return '!' + key + '!';
    }
  }
  
}

  
