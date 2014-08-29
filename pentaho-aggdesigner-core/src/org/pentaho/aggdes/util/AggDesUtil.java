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

package org.pentaho.aggdes.util;

import java.math.BigInteger;
import java.util.List;
import java.util.BitSet;

/**
 * Miscellaneous utility functions for the Aggregate Designer.
 *
 * @author jhyde
 * @version $Id: BitSetPlus.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Aug 14, 2006
 */
public class AggDesUtil extends BitSet {
    private AggDesUtil() {
    }

    /**
     * Computes the
     * <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial
     * coefficient</a>.
     *
     * <p>Informally, if there are {@code n} pizza toppings then the binomial
     * coefficient {@code C(n, k)} gives the number of ways to create a pizza
     * that has {@code k} toppings.
     *
     * @param n N
     * @param k R
     * @return binomial coefficient C(n, k)
     */
    public static BigInteger countCombinations(int n, int k) {
        assert n >= 0;
        if (n == 0 || k == 0 || k == n) {
            return BigInteger.ONE;
        }
        if (k < 0 || k > n) {
            return BigInteger.ZERO;
        }
        // Exploiting the identity C(n, k) == C(n, n - k), swap values if it
        // makes the calculation cheaper.
        // easier.
        if (k > n - k) {
            k = n - k;
        }
        // C(n, k) = n . (n - 1) . ... . (n - k + 1) / k . (k - 1) . ... . 1
        // Compute result by starting with (n - k + 1) / 1
        // then multiply by (n - k + 2) / 2
        // then multiply by (n - k + 3) / 3
        // and finally by n / k.
        // The order of operations ensures that each division is exact.
        BigInteger top = BigInteger.valueOf(n - k + 1);
        for (long i = n - k + 2, j = 2; i <= n; i++, j++) {
            top = top.multiply(BigInteger.valueOf(i));
            top = top.divide(BigInteger.valueOf(j));
        }
        return top;
    }

    /**
     * Casts a List to a List with a different element type.
     *
     * @param list List
     * @return List of desired type
     */
    @SuppressWarnings({"unchecked"})
    public static <T> List<T> cast(List<?> list) {
        return (List<T>) list;
    }
}

// AggDesUtil.java
