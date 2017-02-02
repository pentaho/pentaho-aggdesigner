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
* Copyright 2006 - 2016 Pentaho Corporation.  All rights reserved.
*/

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
