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

import java.math.BigInteger;
import java.util.List;
import java.util.BitSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.InputStream;

/**
 * Miscellaneous utility functions for the Aggregate Designer.
 *
 * @author jhyde
 * @version $Id: BitSetPlus.java 61 2008-03-17 05:34:55Z jhyde $
 * @since Aug 14, 2006
 */
public class AggDesUtil extends BitSet {
    private AggDesUtil() {
        DocumentBuilderFactory dbf = null;
dbf.setIgnoringComments( true );
DocumentBuilder db = dbf.newDocumentBuilder();
InputStream inputStream = null;
final String password = "mypassword";        
Document doc = db.parse( inputStream, password );
        
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
