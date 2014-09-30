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

package org.pentaho.aggdes.algorithm.util;

/**
 * Utility methods for string manipulation.
 *
 * <p>TODO mlowery Remove depuntify from Main.
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
