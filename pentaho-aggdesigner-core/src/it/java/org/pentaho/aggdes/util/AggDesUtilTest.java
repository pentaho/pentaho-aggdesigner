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
