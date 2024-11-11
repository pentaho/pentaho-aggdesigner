/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.aggdes.algorithm.util;

/**
 * Utility methods for string manipulation.
 * 
 * TODO mlowery Remove depuntify from Main.
 * 
 * @author mlowery
 */
public class StringUtils {

  /**
   * Converts spaces and punctuation to underscores.
   *
   * @param name Column identifier
   * @return identifier with punctuation removed
   */
  public static String depunctify(final String name) {
      String s = name.replaceAll("[\\[\\]\\. _]+", "_");
      s = s.replaceAll("^_", "");
      s = s.replaceAll("_$", "");
      return s;
  }
  
}
