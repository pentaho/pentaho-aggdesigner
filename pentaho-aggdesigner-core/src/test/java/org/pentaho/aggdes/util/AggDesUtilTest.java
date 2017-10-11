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

package org.pentaho.aggdes.util;

import junit.framework.TestCase;

import java.math.BigInteger;

/**
 * Unit tests for {@link org.pentaho.aggdes.util.AggDesUtil}.
 *
 * @author jhyde
 * @version $Id: BitSetPlus.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Aug 14, 2006
 */
public class AggDesUtilTest extends TestCase {
    /**
     * Tests {@link AggDesUtil#countCombinations(int,int)} method.
     */
    public static void testCountCombinations() {
        // Pascal's triangle:
        // n \ r  0  1  2  3  4  5  6
        // ------ -------------------
        // 1      1
        // 2      1  2  1
        // 3      1  3  3  1
        // 4      1  4  6  4  1
        // 5      1  5 10 10  5  1
        // 6      1  6 15 20 15  6  1
        for (int n = 1; n < 100; n++) {
            for (int r = 0; r <= n; r++) {
                final BigInteger x = AggDesUtil.countCombinations(n, r);

                // check that pascal's rule holds:
                //   C(n + 1, r + 1) = C(n, r) + C(n, r + 1).
                assertEquals(
                    AggDesUtil.countCombinations(n + 1, r + 1),
                    x.add(
                        AggDesUtil.countCombinations(n, r + 1)));

                if (r == 0 || r == n) {
                    assertEquals(
                        x,
                        BigInteger.ONE);
                }

                if (r == 1 || r == n - 1) {
                    assertEquals(
                        x,
                        BigInteger.valueOf(n));
                }
            }
        }
        assertEquals(
            BigInteger.valueOf(15),
            AggDesUtil.countCombinations(6, 2));
        assertEquals(
            BigInteger.valueOf(20),
            AggDesUtil.countCombinations(6, 3));
        assertEquals(
            new BigInteger("100891344545564193334812497256"),
            AggDesUtil.countCombinations(100, 50));
    }
}

// AggDesUtilTest.java
