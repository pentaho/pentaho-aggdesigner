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

/**
 * <code>BitSetPlus</code> is an extension to {@link java.util.BitSet}.
 *
 * @author jhyde
 * @version $Id: BitSetPlus.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Aug 14, 2006
 */
public class BitSetPlus extends BitSet {
    private final int bitCount;
    private static BigInteger[] twoPowers = {
        BigInteger.ONE,
        BigInteger.valueOf(2),
        BigInteger.valueOf(4),
        BigInteger.valueOf(8),
    };

    public BitSetPlus() {
        this(0);
    }

    public BitSetPlus(int bitCount) {
        super(bitCount);
        this.bitCount = bitCount;
    }

    public int getBitCount() {
        return bitCount;
    }

    /**
     * Returns the number of bitsets which are supersets of <code>bitSet</code>.
     *
     * <p>For example, <code>supersetCardinality(4, {0, 1, 1, 0})</code> is 4,
     * because the supersets are {{0, 1, 1, 0}, {0, 1, 1, 1}, {1, 1, 1, 0},
     * {1, 1, 1, 1}}.
     *
     * <p>The formula is 2 ^ number of zero bits.</p>
     *
     * @return the number of bitsets which are supersets of <code>bitSet</code>
     *   (including the bitset itself and the full set).
     */
    public BigInteger supersetCardinality() {
        // Number of 1's.
        int oneCount = cardinality();
        int zeroCount = bitCount - oneCount;
        return getTwoPower(zeroCount);
    }

    /**
     * Returns the number of bitsets which are supersets of this BitSetPlus
     * but which are not supersets of any of the sets in
     * <code>minusBitSets</code>.
     *
     * <p>This method takes time and space exponential in the cardinality of
     * <code>minusBitSets</code>.
     *
     * @param minusBitSets List of bit sets to subtract
     * @return Cardinality
     */
    public BigInteger countSupersetDiff(List<BitSetPlus> minusBitSets) {
        // If any of the bitsets is a subset or equal to this, there
        // are clearly no supersets.
        for (int i = 0; i < minusBitSets.size(); i++) {
            BitSetPlus minusBitSet = minusBitSets.get(i);
            if (this.contains(minusBitSet)) {
                return BigInteger.ZERO;
            }
        }
        BitSetPlus[] bitSets =
            minusBitSets.toArray(new BitSetPlus[minusBitSets.size()]);
        BigInteger res = supersetCardinality();
        if (true) {
            System.out.println("arity=0" +
                ", total=" + res);
        }
        boolean neg = true;
        for (int arity = 1; arity <= bitSets.length; arity++) {
            BigInteger count = new Work(this, arity + 1, bitSets).doIt();
            if (neg) {
                res = res.subtract(count);
            } else {
                res = res.add(count);
            }
            if (true) {
                System.out.println("arity=" + arity +
                    ", count=" + (neg ? "-" : "") + count +
                    ", total=" + res);
            }
            neg = !neg;
        }
        return res;
    }

    /**
     * Returns true if every bit in this set is also set in <code>bitSet</code>.
     */
    public boolean contains(BitSet bitSet) {
        if (bitSet.isEmpty()) {
            return true;
        }
        if (!intersects(bitSet)) {
            return false;
        }
        for (int nextSetBit = bitSet.nextSetBit(0);
            nextSetBit >= 0;
            nextSetBit = bitSet.nextSetBit(nextSetBit + 1)) {
            if (!get(nextSetBit)) {
                return false;
            }
        }
        return true;
    }

    private static class Work {
        private final BitSetPlus bitSet;
        private final int bitCount;
        private final int arity;
        private final BitSetPlus[] bitSets;
        private final BitSetPlus[] bitSetTuples;
        private BigInteger res;

        Work(BitSetPlus bitSet, int arity, BitSetPlus[] bitSets) {
            this.bitSet = bitSet;
            this.bitCount = bitSet.bitCount;
            this.arity = arity;
            this.bitSets = bitSets;
            this.bitSetTuples = new BitSetPlus[arity];
            this.res = BigInteger.ZERO;
        }

        /**
         * For each combination <code>bitSetTuples</code> consisting of
         * precisely <code>arity</code> sets out of bitSets, computes the
         * number of supersets of bitSet which are not supersets of any member
         * of <code>bitSetTuples</code>.
         */
        BigInteger doIt() {
            this.bitSetTuples[0] = bitSet;
            doIt(1, 0);
            return res;
        }

        /**
         * Enumerates all combinations of <code>bitSetTuples</code>,
         * setting the <code>i</code>th element then recursing to the
         * <code>i</code> + 1st.
         * @param i
         * @param start
         */
        private void doIt(int i, int start) {
            if (i == arity) {
                BigInteger count = countSupersetIntersection(bitCount, bitSetTuples);
                res = res.add(count);
            } else {
                for (int j = start; j < bitSets.length; j++) {
                    bitSetTuples[i] = bitSets[j];
                    doIt(i + 1, j + 1);
                }
            }
        }
    }

    /**
     * Returns the number of sets which are supersets of at least one of the
     * members of <code>bitSets</code>.
     *
     * <p>Example 1. Consider bitset0 = {0, 1, 1, 0}, bitset1 = {1, 0, 1, 1}.
     * The supersets look like {0 or 1, 0 or 1, 1, 0 or 1}.
     *
     * @param bitCount Number of bits in each bitset
     * @param bitSets List of bitsets
     * @return Number of sets in superset
     */
    public static BigInteger countSupersetIntersection_old(
        int bitCount, BitSetPlus[] bitSets)
    {
        int x = 0;
        outer:
        for (int i = 0; i < bitCount; ++i) {
            for (BitSetPlus bitSet : bitSets) {
                if (bitSet.get(i)) {
                    continue outer;
                }
            }
            // All of the bit sets have a zero at this position. Therefore their
            // supersets can all move between 0 and 1.
            ++x;
        }
        return getTwoPower(x);
    }

    /**
     * Returns the number of sets which are supersets of every member of
     * <code>bitSets</code>.
     *
     * <p>Example 1. Consider bitset0 = {0, 1, 1, 0}, bitset1 = {1, 0, 1, 1}.
     * The supersets look like {0 or 1, 0 or 1, 1, 0 or 1}.
     *
     * @param bitCount Number of bits in each bitset
     * @param bitSets List of bitsets
     * @return Number of sets in superset
     */
    public static BigInteger countSupersetIntersection(
        int bitCount, BitSetPlus[] bitSets)
    {
        if (bitSets.length == 0) {
            return getTwoPower(bitCount);
        }
        BitSetPlus total = (BitSetPlus) bitSets[0].clone();
        int nextClearBit = 0;
        for (int i = 1; i < bitSets.length; i++) {
            BitSetPlus set = bitSets[i];
            total.or(set);
            nextClearBit = total.nextClearBit(nextClearBit);
            if (nextClearBit < 0) {
                break; // all bits are set
            }
        }
        int oneCount = total.cardinality();
        int zeroCount = bitCount - oneCount;
        return getTwoPower(zeroCount);
    }

    /**
     * Returns 2 ^ <code>x</code>, using a cached array.
     */
    private static synchronized BigInteger getTwoPower(int x) {
        if (x >= twoPowers.length) {
            System.out.println("getTwoPower: x=" + x + ", length=" + twoPowers.length);
            BigInteger[] olds = twoPowers;
            BigInteger[] news = new BigInteger[Math.max(olds.length * 2 + 1, x + 1)];
            System.arraycopy(olds, 0, news, 0, olds.length);
            BigInteger power = olds[olds.length - 1];
            for (int i = olds.length; i < news.length; i++) {
                news[i] = power = power.add(power);
            }
            twoPowers = news;
        }
        return twoPowers[x];
    }
}

// End BitSetPlus.java
