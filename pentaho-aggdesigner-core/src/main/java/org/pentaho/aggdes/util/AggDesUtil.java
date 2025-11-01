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
