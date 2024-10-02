/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.hash;

//import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
//import static org.junit.Assert.assertThrows;

//import com.google.common.base.Stopwatch;
//import com.google.common.collect.ImmutableSet;
import com.google.common.hash.BloomFilterStrategies.LockFreeBitArray;
import com.google.common.math.LongMath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
//import com.google.common.primitives.Ints;
import com.google.common.testing.EqualsTester;
//import com.google.common.testing.NullPointerTester;
import com.google.common.testing.SerializableTester;
//import com.google.common.util.concurrent.Uninterruptibles;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//import junit.framework.TestCase;
//import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Tests for SimpleGenericBloomFilter and derived BloomFilter views.
 *
 * @author Dimitris Andreou
 */
public class BloomFilterTest /* extends TestCase */ {
    private static final int NUM_PUTS = 100_000;
    private static final ThreadLocal<Random> random =
            new ThreadLocal<Random>() {
                @Override
                protected Random initialValue() {
                    return new Random();
                }
            };

    private static final int GOLDEN_PRESENT_KEY = random.get().nextInt();

//    @AndroidIncompatible // OutOfMemoryError
    @Test public void testLargeBloomFilterDoesntOverflow() {
        long numBits = Integer.MAX_VALUE;
        numBits++;

        LockFreeBitArray bitArray = new LockFreeBitArray(numBits);
        Assertions.assertTrue(bitArray.bitSize() > 0,
                "BitArray.bitSize() must return a positive number, but was " + bitArray.bitSize());

        // Ideally we would also test the bitSize() overflow of this BF, but it runs out of heap space
        // BloomFilter.create(Funnels.unencodedCharsFunnel(), 244412641, 1e-11);
    }

    /**
     * Asserts that {@link BloomFilter#approximateElementCount} is within 1 percent of the expected
     * value.
     */
    private static void assertApproximateElementCountGuess(BloomFilter<?> bf, int sizeGuess) {
//        assertThat(bf.approximateElementCount()).isAtLeast((long) (sizeGuess * 0.99));
        Assertions.assertTrue(bf.approximateElementCount() > (long) (sizeGuess * 0.99));
//        assertThat(bf.approximateElementCount()).isAtMost((long) (sizeGuess * 1.01));
        Assertions.assertTrue(bf.approximateElementCount() < (long) (sizeGuess * 1.01));
    }

    @Test public void testCreateAndCheckMitz32BloomFilterWithKnownFalsePositives() {
        int numInsertions = 1000000;
        BloomFilter<String> bf =
                BloomFilter.create(
                        Funnels.unencodedCharsFunnel(),
                        numInsertions,
                        0.03,
                        BloomFilterStrategies.MURMUR128_MITZ_32);

        // Insert "numInsertions" even numbers into the BF.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            bf.put(Integer.toString(i));
        }
        assertApproximateElementCountGuess(bf, numInsertions);

        // Assert that the BF "might" have all of the even numbers.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            Assertions.assertTrue(bf.mightContain(Integer.toString(i)));
        }

        // Now we check for known false positives using a set of known false positives.
        // (These are all of the false positives under 900.)
        /* Immutable */ Set<Integer> falsePositives = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(49, 51, 59, 163, 199, 321, 325, 363, 367, 469, 545, 561, 727, 769, 773, 781)));
//                ImmutableSet.of(
//                        49, 51, 59, 163, 199, 321, 325, 363, 367, 469, 545, 561, 727, 769, 773, 781);
        for (int i = 1; i < 900; i += 2) {
            if (!falsePositives.contains(i)) {
                Assertions.assertFalse(bf.mightContain(Integer.toString(i)), "BF should not contain " + i);
            }
        }

        // Check that there are exactly 29824 false positives for this BF.
        int knownNumberOfFalsePositives = 29824;
        int numFpp = 0;
        for (int i = 1; i < numInsertions * 2; i += 2) {
            if (bf.mightContain(Integer.toString(i))) {
                numFpp++;
            }
        }
        Assertions.assertEquals(knownNumberOfFalsePositives, numFpp);
        double expectedReportedFpp = (double) knownNumberOfFalsePositives / numInsertions;
        double actualReportedFpp = bf.expectedFpp();
//        assertThat(actualReportedFpp).isWithin(0.00015).of(expectedReportedFpp);
        double delta = Math.abs(actualReportedFpp - expectedReportedFpp);
        Assertions.assertTrue(delta >= 0 && delta <= 0.00015);
    }

    @Test public void testCreateAndCheckBloomFilterWithKnownFalsePositives64() {
        int numInsertions = 1000000;
        BloomFilter<String> bf =
                BloomFilter.create(
                        Funnels.unencodedCharsFunnel(),
                        numInsertions,
                        0.03,
                        BloomFilterStrategies.MURMUR128_MITZ_64);

        // Insert "numInsertions" even numbers into the BF.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            bf.put(Integer.toString(i));
        }
        assertApproximateElementCountGuess(bf, numInsertions);

        // Assert that the BF "might" have all of the even numbers.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            Assertions.assertTrue(bf.mightContain(Integer.toString(i)));
        }

        // Now we check for known false positives using a set of known false positives.
        // (These are all of the false positives under 900.)
        /* Immutable */Set<Integer> falsePositives = Arrays.asList(15, 25, 287, 319, 381, 399, 421, 465, 529, 697, 767, 857)
                .stream().collect(Collectors.toSet());
//                ImmutableSet.of(15, 25, 287, 319, 381, 399, 421, 465, 529, 697, 767, 857);
        for (int i = 1; i < 900; i += 2) {
            if (!falsePositives.contains(i)) {
                Assertions.assertFalse(bf.mightContain(Integer.toString(i)), "BF should not contain " + i);
            }
        }

        // Check that there are exactly 30104 false positives for this BF.
        int knownNumberOfFalsePositives = 30104;
        int numFpp = 0;
        for (int i = 1; i < numInsertions * 2; i += 2) {
            if (bf.mightContain(Integer.toString(i))) {
                numFpp++;
            }
        }
        Assertions.assertEquals(knownNumberOfFalsePositives, numFpp);
        double expectedReportedFpp = (double) knownNumberOfFalsePositives / numInsertions;
        double actualReportedFpp = bf.expectedFpp();
//        assertThat(actualReportedFpp).isWithin(0.00033).of(expectedReportedFpp);
        double delta = Math.abs(actualReportedFpp - expectedReportedFpp);
        Assertions.assertTrue(delta >= 0 && delta <= 0.00033);
    }

    @Test public void testCreateAndCheckBloomFilterWithKnownUtf8FalsePositives64() {
        int numInsertions = 1000000;
        BloomFilter<String> bf =
                BloomFilter.create(
                        Funnels.stringFunnel(UTF_8),
                        numInsertions,
                        0.03,
                        BloomFilterStrategies.MURMUR128_MITZ_64);

        // Insert "numInsertions" even numbers into the BF.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            bf.put(Integer.toString(i));
        }
        assertApproximateElementCountGuess(bf, numInsertions);

        // Assert that the BF "might" have all of the even numbers.
        for (int i = 0; i < numInsertions * 2; i += 2) {
            Assertions.assertTrue(bf.mightContain(Integer.toString(i)));
        }

        // Now we check for known false positives using a set of known false positives.
        // (These are all of the false positives under 900.)
        /* Immutable */Set<Integer> falsePositives = Arrays.asList(129, 471, 723, 89, 751, 835, 871)
                .stream().collect(Collectors.toSet()); // ImmutableSet.of(129, 471, 723, 89, 751, 835, 871);
        for (int i = 1; i < 900; i += 2) {
            if (!falsePositives.contains(i)) {
                Assertions.assertFalse(bf.mightContain(Integer.toString(i)), "BF should not contain " + i);
            }
        }

        // Check that there are exactly 29763 false positives for this BF.
        int knownNumberOfFalsePositives = 29763;
        int numFpp = 0;
        for (int i = 1; i < numInsertions * 2; i += 2) {
            if (bf.mightContain(Integer.toString(i))) {
                numFpp++;
            }
        }
        Assertions.assertEquals(knownNumberOfFalsePositives, numFpp);
        double expectedReportedFpp = (double) knownNumberOfFalsePositives / numInsertions;
        double actualReportedFpp = bf.expectedFpp();
//        assertThat(actualReportedFpp).isWithin(0.00033).of(expectedReportedFpp);
        double delta = Math.abs(actualReportedFpp - expectedReportedFpp);
        Assertions.assertTrue(delta >= 0 && delta <= 0.00033);
    }

    /** Sanity checking with many combinations of false positive rates and expected insertions */
    @Test public void testBasic() {
        for (double fpr = 0.0000001; fpr < 0.1; fpr *= 10) {
            for (int expectedInsertions = 1; expectedInsertions <= 10000; expectedInsertions *= 10) {
                checkSanity(BloomFilter.create(HashTestUtils.BAD_FUNNEL, expectedInsertions, fpr));
            }
        }
    }

    @Test public void testPreconditions() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> BloomFilter.create(Funnels.unencodedCharsFunnel(), -1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> BloomFilter.create(Funnels.unencodedCharsFunnel(), -1, 0.03));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> BloomFilter.create(Funnels.unencodedCharsFunnel(), 1, 0.0));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> BloomFilter.create(Funnels.unencodedCharsFunnel(), 1, 1.0));
    }

    @Test public void testFailureWhenMoreThan255HashFunctionsAreNeeded() {
        int n = 1000;
        double p = 0.00000000000000000000000000000000000000000000000000000000000000000000000000000001;
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    BloomFilter.create(Funnels.unencodedCharsFunnel(), n, p);
                });
    }

//    public void testNullPointers() {
//        NullPointerTester tester = new NullPointerTester();
//        tester.testAllPublicInstanceMethods(BloomFilter.create(Funnels.unencodedCharsFunnel(), 100));
//        tester.testAllPublicStaticMethods(BloomFilter.class);
//    }

    /** Tests that we never get an optimal hashes number of zero. */
    public void testOptimalHashes() {
        for (int n = 1; n < 1000; n++) {
            for (int m = 0; m < 1000; m++) {
                Assertions.assertTrue(BloomFilter.optimalNumOfHashFunctions(n, m) > 0);
            }
        }
    }

    // https://code.google.com/p/guava-libraries/issues/detail?id=1781
    @Test public void testOptimalNumOfHashFunctionsRounding() {
        Assertions.assertEquals(7, BloomFilter.optimalNumOfHashFunctions(319, 3072));
    }

    /** Tests that we always get a non-negative optimal size. */
    @Test public void testOptimalSize() {
        for (int n = 1; n < 1000; n++) {
            for (double fpp = Double.MIN_VALUE; fpp < 1.0; fpp += 0.001) {
                Assertions.assertTrue(BloomFilter.optimalNumOfBits(n, fpp) >= 0);
            }
        }

        // some random values
        Random random = new Random(0);
        for (int repeats = 0; repeats < 10000; repeats++) {
            Assertions.assertTrue(BloomFilter.optimalNumOfBits(random.nextInt(1 << 16), random.nextDouble()) >= 0);
        }

        // and some crazy values (this used to be capped to Integer.MAX_VALUE, now it can go bigger
        Assertions.assertEquals(3327428144502L, BloomFilter.optimalNumOfBits(Integer.MAX_VALUE, Double.MIN_VALUE));
        IllegalArgumentException expected =
                Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            BloomFilter<String> unused =
                                    BloomFilter.create(HashTestUtils.BAD_FUNNEL, Integer.MAX_VALUE, Double.MIN_VALUE);
                        });
//        assertThat(expected)
//                .hasMessageThat()
//                .isEqualTo("Could not create BloomFilter of 3327428144502 bits");
        Assertions.assertEquals("Could not create BloomFilter of 3327428144502 bits", expected.getMessage());
    }

//    @AndroidIncompatible // OutOfMemoryError
    @Test public void testLargeNumberOfInsertions() {
        // We use horrible FPPs here to keep Java from OOM'ing
        BloomFilter<String> unused =
                BloomFilter.create(Funnels.unencodedCharsFunnel(), Integer.MAX_VALUE / 2, 0.30);
        unused = BloomFilter.create(Funnels.unencodedCharsFunnel(), 45L * Integer.MAX_VALUE, 0.99);
    }

    private static void checkSanity(BloomFilter<Object> bf) {
        Assertions.assertFalse(bf.mightContain(new Object()));
        Assertions.assertFalse(bf.test(new Object()));
        for (int i = 0; i < 100; i++) {
            Object o = new Object();
            bf.put(o);
            Assertions.assertTrue(bf.mightContain(o));
            Assertions.assertTrue(bf.test(o));
        }
    }

    @Test public void testCopy() {
        BloomFilter<String> original = BloomFilter.create(Funnels.unencodedCharsFunnel(), 100);
        BloomFilter<String> copy = original.copy();
        Assertions.assertNotSame(original, copy); // assertNotSame(original, copy);
        Assertions.assertEquals(original, copy);
    }

    @Test public void testExpectedFpp() {
        BloomFilter<Object> bf = BloomFilter.create(HashTestUtils.BAD_FUNNEL, 10, 0.03);
        double fpp = bf.expectedFpp();
        Assertions.assertTrue(Double.compare(fpp, 0.0) == 0); // assertThat(fpp).isEqualTo(0.0);
        // usually completed in less than 200 iterations
        while (fpp != 1.0) {
            boolean changed = bf.put(new Object());
            double newFpp = bf.expectedFpp();
            // if changed, the new fpp is strictly higher, otherwise it is the same
            Assertions.assertTrue(changed ? newFpp > fpp : newFpp == fpp);
            fpp = newFpp;
        }
    }

//    @AndroidIncompatible // slow
    @Test public void testBitSize() {
        double fpp = 0.03;
        for (int i = 1; i < 10000; i++) {
            long numBits = BloomFilter.optimalNumOfBits(i, fpp);
//            int arraySize = Ints.checkedCast(LongMath.divide(numBits, 64, RoundingMode.CEILING));
            long tmp = LongMath.divide(numBits, 64, RoundingMode.CEILING);
            int arraySize = (int) tmp;
            if (tmp != arraySize) {
                throw new IllegalArgumentException(String.format("Out of range: %s", tmp));
            }
            Assertions.assertEquals(
                    arraySize * Long.SIZE,
                    BloomFilter.create(Funnels.unencodedCharsFunnel(), i, fpp).bitSize());
        }
    }

    @Test public void testApproximateElementCount() {
        int numInsertions = 1000;
        BloomFilter<Integer> bf = BloomFilter.create(Funnels.integerFunnel(), numInsertions);
        bf.put(-1);
        for (int i = 0; i < numInsertions; i++) {
            bf.put(i);
        }
        assertApproximateElementCountGuess(bf, numInsertions);
    }

    @Test public void testEquals_empty() {
        new EqualsTester()
                .addEqualityGroup(BloomFilter.create(Funnels.byteArrayFunnel(), 100, 0.01))
                .addEqualityGroup(BloomFilter.create(Funnels.byteArrayFunnel(), 100, 0.02))
                .addEqualityGroup(BloomFilter.create(Funnels.byteArrayFunnel(), 200, 0.01))
                .addEqualityGroup(BloomFilter.create(Funnels.byteArrayFunnel(), 200, 0.02))
                .addEqualityGroup(BloomFilter.create(Funnels.unencodedCharsFunnel(), 100, 0.01))
                .addEqualityGroup(BloomFilter.create(Funnels.unencodedCharsFunnel(), 100, 0.02))
                .addEqualityGroup(BloomFilter.create(Funnels.unencodedCharsFunnel(), 200, 0.01))
                .addEqualityGroup(BloomFilter.create(Funnels.unencodedCharsFunnel(), 200, 0.02))
                .testEquals();
    }

    @Test public void testEquals() {
        BloomFilter<String> bf1 = BloomFilter.create(Funnels.unencodedCharsFunnel(), 100);
        bf1.put("1");
        bf1.put("2");

        BloomFilter<String> bf2 = BloomFilter.create(Funnels.unencodedCharsFunnel(), 100);
        bf2.put("1");
        bf2.put("2");

        new EqualsTester().addEqualityGroup(bf1, bf2).testEquals();

        bf2.put("3");

        new EqualsTester().addEqualityGroup(bf1).addEqualityGroup(bf2).testEquals();
    }

    @Test public void testEqualsWithCustomFunnel() {
        BloomFilter<Long> bf1 = BloomFilter.create(new CustomFunnel(), 100);
        BloomFilter<Long> bf2 = BloomFilter.create(new CustomFunnel(), 100);
        Assertions.assertEquals(bf1, bf2);
    }

    @Test public void testSerializationWithCustomFunnel() {
        SerializableTester.reserializeAndAssert(BloomFilter.create(new CustomFunnel(), 100));
    }

    private static final class CustomFunnel implements Funnel<Long> {
        @Override
        public void funnel(Long value, PrimitiveSink into) {
            into.putLong(value);
        }

        @Override
        public boolean equals(/* @Nullable */ Object object) {
            return (object instanceof CustomFunnel);
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    @Test public void testPutReturnValue() {
        for (int i = 0; i < 10; i++) {
            BloomFilter<String> bf = BloomFilter.create(Funnels.unencodedCharsFunnel(), 100);
            for (int j = 0; j < 10; j++) {
                String value = new Object().toString();
                boolean mightContain = bf.mightContain(value);
                boolean put = bf.put(value);
                Assertions.assertTrue(mightContain != put);
            }
        }
    }

    @Test public void testPutAll() {
        int element1 = 1;
        int element2 = 2;

        BloomFilter<Integer> bf1 = BloomFilter.create(Funnels.integerFunnel(), 100);
        bf1.put(element1);
        Assertions.assertTrue(bf1.mightContain(element1));
        Assertions.assertFalse(bf1.mightContain(element2));

        BloomFilter<Integer> bf2 = BloomFilter.create(Funnels.integerFunnel(), 100);
        bf2.put(element2);
        Assertions.assertFalse(bf2.mightContain(element1));
        Assertions.assertTrue(bf2.mightContain(element2));

        Assertions.assertTrue(bf1.isCompatible(bf2));
        bf1.putAll(bf2);
        Assertions.assertTrue(bf1.mightContain(element1));
        Assertions.assertTrue(bf1.mightContain(element2));
        Assertions.assertFalse(bf2.mightContain(element1));
        Assertions.assertTrue(bf2.mightContain(element2));
    }

    @Test public void testPutAllDifferentSizes() {
        BloomFilter<Integer> bf1 = BloomFilter.create(Funnels.integerFunnel(), 1);
        BloomFilter<Integer> bf2 = BloomFilter.create(Funnels.integerFunnel(), 10);

        Assertions.assertFalse(bf1.isCompatible(bf2));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    bf1.putAll(bf2);
                });

        Assertions.assertFalse(bf2.isCompatible(bf1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    bf2.putAll(bf1);
                });
    }

    @Test public void testPutAllWithSelf() {
        BloomFilter<Integer> bf1 = BloomFilter.create(Funnels.integerFunnel(), 1);
        Assertions.assertFalse(bf1.isCompatible(bf1));
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    bf1.putAll(bf1);
                });
    }

    @Test public void testJavaSerialization() {
        BloomFilter<byte[]> bf = BloomFilter.create(Funnels.byteArrayFunnel(), 100);
        for (int i = 0; i < 10; i++) {
//            bf.put(Ints.toByteArray(i));
            bf.put(new byte[] {(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i});
        }

        BloomFilter<byte[]> copy = SerializableTester.reserialize(bf);
        for (int i = 0; i < 10; i++) {
//            Assertions.assertTrue(copy.mightContain(Ints.toByteArray(i)));
            Assertions.assertTrue(copy.mightContain(new byte[] {(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i}));
        }
        // assertThat(copy.expectedFpp()).isEqualTo(bf.expectedFpp());
        Assertions.assertEquals(0, Double.compare(copy.expectedFpp(), bf.expectedFpp()));

        SerializableTester.reserializeAndAssert(bf);
    }

    @Test public void testCustomSerialization() throws Exception {
        Funnel<byte[]> funnel = Funnels.byteArrayFunnel();
        BloomFilter<byte[]> bf = BloomFilter.create(funnel, 100);
        for (int i = 0; i < 100; i++) {
//            bf.put(Ints.toByteArray(i));
            bf.put(new byte[] {(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i});
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bf.writeTo(out);

        BloomFilter<byte[]> read =
                BloomFilter.readFrom(new ByteArrayInputStream(out.toByteArray()), funnel);
        Assertions.assertEquals(read, bf); // assertThat(read).isEqualTo(bf);
        Assertions.assertTrue(read.expectedFpp() > 0); // assertThat(read.expectedFpp()).isGreaterThan(0);
    }

    /**
     * This test will fail whenever someone updates/reorders the BloomFilterStrategies constants. Only
     * appending a new constant is allowed.
     */
    @Test public void testBloomFilterStrategies() {
        // assertThat(BloomFilterStrategies.values()).hasLength(2);
        Assertions.assertEquals(2, BloomFilterStrategies.values().length);
        Assertions.assertEquals(BloomFilterStrategies.MURMUR128_MITZ_32, BloomFilterStrategies.values()[0]);
        Assertions.assertEquals(BloomFilterStrategies.MURMUR128_MITZ_64, BloomFilterStrategies.values()[1]);
    }


    @Test public void testNoRaceConditions() throws Exception {
        final BloomFilter<Integer> bloomFilter =
                BloomFilter.create(Funnels.integerFunnel(), 15_000_000, 0.01);

        // This check has to be BEFORE the loop because the random insertions can
        // flip GOLDEN_PRESENT_KEY to true even if it wasn't explicitly inserted
        // (false positive).
//        assertThat(bloomFilter.mightContain(GOLDEN_PRESENT_KEY)).isFalse();
        Assertions.assertFalse(bloomFilter.mightContain(GOLDEN_PRESENT_KEY));
        for (int i = 0; i < NUM_PUTS; i++) {
            bloomFilter.put(getNonGoldenRandomKey());
        }
        bloomFilter.put(GOLDEN_PRESENT_KEY);

        int numThreads = 12;
        final double safetyFalsePositiveRate = 0.1;
        long stopWatch = System.currentTimeMillis(); // final Stopwatch stopwatch = Stopwatch.createStarted();

        Runnable task =
                new Runnable() {
                    @Override
                    public void run() {
                        do {
                            // We can't have a GOLDEN_NOT_PRESENT_KEY because false positives are
                            // possible! It's false negatives that can't happen.
//                            assertThat(bloomFilter.mightContain(GOLDEN_PRESENT_KEY)).isTrue();
                            Assertions.assertTrue(bloomFilter.mightContain(GOLDEN_PRESENT_KEY));

                            int key = getNonGoldenRandomKey();
                            // We can't check that the key is mightContain() == false before the
                            // put() because the key could have already been generated *or* the
                            // bloom filter might say true even when it's not there (false
                            // positive).
                            bloomFilter.put(key);
                            // False negative should *never* happen.
//                            assertThat(bloomFilter.mightContain(key)).isTrue();
                            Assertions.assertTrue(bloomFilter.mightContain(key));

                            // If this check ever fails, that means we need to either bump the
                            // number of expected insertions or don't run the test for so long.
                            // Don't forget, the bloom filter slowly saturates over time and the
                            // expected false positive probability goes up!
//                            assertThat(bloomFilter.expectedFpp()).isLessThan(safetyFalsePositiveRate);
                            Assertions.assertTrue(Double.compare(bloomFilter.expectedFpp(), safetyFalsePositiveRate) < 0);
//                        } while (stopwatch.elapsed(TimeUnit.SECONDS) < 1);
                        } while (System.currentTimeMillis() - stopWatch < 1000);
                    }
                };

        List<Throwable> exceptions = runThreadsAndReturnExceptions(numThreads, task);

        Assertions.assertTrue(exceptions.isEmpty()); // assertThat(exceptions).isEmpty();
    }

    private static List<Throwable> runThreadsAndReturnExceptions(int numThreads, Runnable task) {
        List<Thread> threads = new ArrayList<>(numThreads);
        final List<Throwable> exceptions = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(task);
            thread.setUncaughtExceptionHandler(
                    new UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread t, Throwable e) {
                            exceptions.add(e);
                        }
                    });
            threads.add(thread);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
//            Uninterruptibles.joinUninterruptibly(t);
            joinUninterruptibly(t);
        }
        return exceptions;
    }

    private static int getNonGoldenRandomKey() {
        int key;
        do {
            key = random.get().nextInt();
        } while (key == GOLDEN_PRESENT_KEY);
        return key;
    }

    // copy from guava/src/com/google/common/util/concurrent/Uninterruptibles.java
    public static void joinUninterruptibly(Thread toJoin) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    toJoin.join();
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}