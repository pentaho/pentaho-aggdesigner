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
* Copyright 2006 - 2013 Pentaho Corporation.  All rights reserved.
*/

package org.pentaho.aggdes.util;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.util.*;

/**
 * Unit test for {@link BitSetPlus}.
 *
 * @author jhyde
 * @version $Id: BitSetPlusTest.java 931 2008-09-24 21:28:47Z mbatchelor $
 * @since Aug 14, 2006
 */
public class BitSetPlusTest extends TestCase {
    public void testSupersetCardinality() {
        // The bitset {0, 1, 0, 1, 1} has supersets
        // {0, 1, 1, 1, 1},
        // {1, 1, 0, 1, 1},
        // {1, 1, 1, 1, 1}.
        // Plus itself makes 4.
        BitSetPlus bitSet = new BitSetPlus(5);
        bitSet.set(1);
        bitSet.set(3);
        bitSet.set(4);
        assertEquals(
            BigInteger.valueOf(4),
            bitSet.supersetCardinality());

        // Every set is a superet of {0, 0, 0, 0, 0}.
        bitSet.clear();
        assertEquals(
            BigInteger.valueOf(32),
            bitSet.supersetCardinality());

        // The bitset {1, 1, 1, 1, 1} has only one superset -- itself.
        bitSet.flip(0, 5);
        assertEquals(
            BigInteger.ONE,
            bitSet.supersetCardinality());
    }

    public void testSupersetIntersection() {
        // Consider bitset0 = {0, 1, 1, 0}, bitset1 = {1, 0, 1, 0}.
        // The supersets look like {1, 1, 1, 0 or 1}.
        BitSetPlus bitset0110 = new BitSetPlus(4);
        bitset0110.set(1);
        bitset0110.set(2);

        BitSetPlus bitset1010 = new BitSetPlus(4);
        bitset1010.set(0);
        bitset1010.set(2);

        assertEquals(
            BigInteger.valueOf(2),
            BitSetPlus.countSupersetIntersection(
                4, new BitSetPlus[] {bitset0110, bitset1010}));

        // As above, but exhaustively.
        assertEquals(
            2,
            countSupersetIntersectionExhaustively(4, bitset0110, bitset1010));

        Random random = new Random(123);
        for (int i = 0; i < 10; ++i) {
            BitSetPlus bs0 = randomBitset(5, random);
            BitSetPlus bs1 = randomBitset(5, random);
            BigInteger actual =
                BitSetPlus.countSupersetIntersection(
                    5,
                    new BitSetPlus[] {bs0, bs1});
            int expected = countSupersetIntersectionExhaustively(5, bs0, bs1);
            assertEquals(BigInteger.valueOf(expected), actual);
        }
    }

    public void testSupersetIntersection2() {
        // Consider
        //   bitset0 = {0, 1, 1, 0},
        //   bitsets = {{0, 0, 1, 0}, {1, 0, 1, 0}}.
        // The supersets look like {1, 1, 1, 0 or 1}.
        BitSetPlus bitset0110 = new BitSetPlus(4);
        bitset0110.set(1);
        bitset0110.set(2);

        BitSetPlus bitset0010 = new BitSetPlus(4);
        bitset0010.set(0);
        bitset0010.set(2);

        BitSetPlus bitset1010 = new BitSetPlus(4);
        bitset1010.set(0);
        bitset1010.set(2);

        BitSetPlus[] bitSets =
            new BitSetPlus[]{bitset0010, bitset1010};

        assertEquals(
            BigInteger.valueOf(4),
            BitSetPlus.countSupersetIntersection(
                4, bitSets));

        // As above, but exhaustively.
        assertEquals(
            4,
            countSupersetIntersectionExhaustively(4, bitSets));

        Random random = new Random(123);
        checkSupsersetIntersection2(0, random);
        checkSupsersetIntersection2(1, random);
        checkSupsersetIntersection2(2, random);
        checkSupsersetIntersection2(3, random);
        checkSupsersetIntersection2(4, random);
        checkSupsersetIntersection2(5, random);
        checkSupsersetIntersection2(6, random);
        checkSupsersetIntersection2(7, random);
        checkSupsersetIntersection2(8, random);
        checkSupsersetIntersection2(9, random);
        checkSupsersetIntersection2(10, random);
    }

    private void checkSupsersetIntersection2(int i, Random random) {
        BitSetPlus[] bitSets;
        bitSets = new BitSetPlus[i];
        for (int j = 0; j < i; j++) {
            bitSets[j] = randomBitset(5, random);
        }
        BigInteger actual =
            BitSetPlus.countSupersetIntersection(5, bitSets);
        int expected = countSupersetIntersectionExhaustively(5, bitSets);
        assertEquals(BigInteger.valueOf(expected), actual);
    }

    private int countSupersetIntersectionExhaustively(
        int bitCount, BitSetPlus bitset0, BitSetPlus bitset1) {

        int count = 0;
        long possibleSets = (1L << bitCount);
        for (long i = 0; i < possibleSets; i++) {
            BitSetPlus bitSet = createBitSet(bitCount, i);
            if (bitSet.contains(bitset0) &&
                bitSet.contains(bitset1)) {
                if (false) System.out.println("bitset=" + bitSet + ", 0=" + bitset0 + ", 1=" + bitset1);
                ++count;
            }
        }
        return count;
    }

    private int countSupersetIntersectionExhaustively(
        int bitCount, BitSetPlus[] bitsets) {

        int count = 0;
        long possibleSets = (1L << bitCount);
        outer:
        for (long i = 0; i < possibleSets; i++) {
            BitSetPlus bitSet = createBitSet(bitCount, i);
            for (BitSetPlus bitset : bitsets) {
                if (!bitSet.contains(bitset)) {
                    continue outer;
                }
            }
            ++count;
        }
        return count;
    }

    private static BitSetPlus createBitSet(int bitCount, long k) {
        BitSetPlus bitSet = new BitSetPlus(bitCount);
        for (int j = 0; k != 0; ++j) {
            if ((k & 1) == 1) {
                bitSet.set(j);
            }
            k >>= 1;
        }
        return bitSet;
    }

    private BitSetPlus randomBitset(int bitCount, Random random) {
        BitSetPlus bitSet = new BitSetPlus(bitCount);
        for (int i = 0; i < bitCount; i++) {
            if (random.nextBoolean()) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    public void testSupersetMinusCardinality() {
        // {0, 1, 0, 1, 0}
        BitSetPlus bitset01010 = new BitSetPlus(5);
        bitset01010.set(1);
        bitset01010.set(3);

        // {0, 1, 1, 0, 1}
        BitSetPlus bitset01101 = new BitSetPlus(5);
        bitset01101.set(1);
        bitset01101.set(2);
        bitset01101.set(4);

        // How many supersets of {0, 1, 0, 1, 0}
        // are not supersets of {0, 1, 1, 0, 1}?
        BigInteger a1 = bitset01010.countSupersetDiff(
            Arrays.asList(bitset01101));
        assertEquals(BigInteger.valueOf(6), a1);

        // As above, but exhaustively.
        assertEquals(
            6,
            countSupersetDiffExhaustively(
                5,
                bitset01010,
                Arrays.asList(bitset01101)));

        // Now a few random ones.
        Random random = new Random(2345);
        checkCountSupersetDiff(3, 1, random, true, true);
        checkCountSupersetDiff(3, 2, random, true, true);
        checkCountSupersetDiff(3, 5, random, true, true);
        checkCountSupersetDiff(3, 10, random, true, true);
        checkCountSupersetDiff(10, 1, random, true, true);
        checkCountSupersetDiff(10, 2, random, true, true);
        checkCountSupersetDiff(10, 3, random, true, true);
        // (10, 22) is too high
        checkCountSupersetDiff(10, 17, random, true, true);
        // (25, 20) is too high
        checkCountSupersetDiff(20, 15, random, true, true);
    }

    public void testSupersetMinusCardinalityLarge() {
        Random random = new Random(2345);
        checkCountSupersetDiff(20, 15, random, true, true);
    }

    /**
     * Checks that for a random bitmap with <code>bitCount</code> bits,
     * and collection of <code>setCount</code> negative bitmaps,
     * the actual algorithm gets the same result as an exhastive algorithm.
     *
     * <p>We recommend that:<ul>
     * <li><code>computeExpected</code> is true only if bitCount &lt; 20
     * (because the exhaustive
     * algorithm requires time exponential in bitCount)
     * <li><code>computeExpected</code> is true only if setCount &lt; 17
     * (because the actual algorithm requires time and space exponential in
     * setCount)
     * </ul>
     *
     * @param bitCount Number of bits total
     * @param setCount Number of bits set
     * @param random Random number generator
     * @param computeExpected Whether to compute expected
     * @param computeActual Whether to compute actual
     */
    private void checkCountSupersetDiff(
        int bitCount,
        int setCount,
        Random random,
        boolean computeExpected,
        boolean computeActual)
    {
        System.out.println("bitCount=" + bitCount + ", setCount=" + setCount);
        BitSetPlus a = createBitSet(bitCount, random, .5);
        List<BitSetPlus> bitSets = new ArrayList<BitSetPlus>(setCount);
        // Choose a density such that any given bit will appear in .5 bitset,
        // on average.
        double density = 1.0 - Math.pow((1.0 - 0.9 / (double) bitCount), (double) setCount);
        for (int j = 0; j < setCount; j++) {
            bitSets.add(createBitSet(bitCount, random, density));
        }

        // Compute the total two different ways, and make sure they match.
        long expected;
        if (computeExpected) {
            expected = countSupersetDiffExhaustively(bitCount,
                a,
                bitSets);
        } else {
            expected = 0;
        }
        BigInteger actual;
        if (computeActual) {
            actual = a.countSupersetDiff(bitSets);
        } else {
            actual = BigInteger.ZERO;
        }
        if (computeExpected && computeActual) {
            if (true) {
                StringBuilder sb = new StringBuilder();
                for (BitSetPlus bs : bitSets) {
                    sb.append(", ").append(bs);
                }
                System.out.println("bs0=" + a + sb + ", expected=" + expected +
                    ", actual=" + actual);
            }
            assertEquals(BigInteger.valueOf(expected), actual);
        }
    }

    private BitSetPlus createHalfFullBitSet(int bitCount, Random random) {
        long max = (1L << bitCount);
        return createBitSet(bitCount, Math.abs(random.nextLong()) % max);
    }

    private BitSetPlus createBitSet(int bitCount, Random random, double density) {
        if (density == .5) {
            long max = (1L << bitCount);
            return createBitSet(bitCount, Math.abs(random.nextLong()) % max);
        }
        BitSetPlus bitSet = new BitSetPlus(bitCount);
        for (int i = 0; i < bitCount; i++) {
            if (random.nextDouble() < density) {
                bitSet.set(i);
            }
        }
        return bitSet;
    }

    /**
     * Counts how many supersets of bs0 are not supersets of any member of
     * bitSets.
     *
     * <p>This method takes time exponential in bitCount.
     */
    private long countSupersetDiffExhaustively(
        int bitCount, BitSetPlus bs0, List<BitSetPlus> bitSets)
    {
        long count = 0;
        long max = 1L << bitCount;
        loop:
        for (long i = 0; i < max; i++) {
            BitSetPlus bitSet = createBitSet(bitCount, i);
            if (!bitSet.contains(bs0)) {
                continue;
            }
            for (BitSetPlus bs : bitSets) {
                if (bitSet.contains(bs)) {
                    continue loop;
                }
            }
            if (true) {
                StringBuilder sb = new StringBuilder();
                for (BitSetPlus bs : bitSets) {
                    sb.append(", ").append(bs);
                }
                System.out.println("bs0=" + bs0 + sb + ", bitSet=" + bitSet);
            }
            ++count;
        }
        return count;
    }

    public void testContains() {
        BitSetPlus bits13 = create(new int[]{1, 3}, 4);
        BitSetPlus bits12 = create(new int[]{1, 2}, 4);
        BitSetPlus bits1 = create(new int[]{1}, 4);
        BitSetPlus bitsEmpty = create(new int[]{}, 4);
        assertTrue(bits13.contains(bits1));
        assertTrue(bits13.contains(bitsEmpty));
        assertFalse(bits13.contains(bits12));
        assertFalse(bitsEmpty.contains(bits1));
    }

    private static BitSetPlus create(int [] bits, int bitCount) {
        BitSetPlus bitSet = new BitSetPlus(bitCount);
        for (int bit : bits) {
            bitSet.set(bit);
        }
        return bitSet;
    }
}

// End BitSetPlusTest.java
