// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.common.hash;

//import static com.google.common.truth.Truth.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

//import com.google.common.base.Strings;
//import com.google.common.collect.ImmutableSortedMap;
//import com.google.common.collect.Ordering;
//import com.google.common.primitives.UnsignedLong;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
//import junit.framework.TestCase;

/**
 * Unit test for Fingerprint2011.
 *
 * @author kylemaddison@google.com (Kyle Maddison)
 */
public class Fingerprint2011Test /* extends TestCase */ {

    // Length of the sample string to produce
    private static final int MAX_BYTES = 1000;

    // Map from sample string lengths to the fingerprint
    private static final /* Immutable */ SortedMap<Integer, Long> LENGTH_FINGERPRINTS = new TreeMap<>();

    static {
//            new ImmutableSortedMap.Builder<Integer, Long>(Ordering.natural())
        LENGTH_FINGERPRINTS.put(1000, 0x433109b33e13e6edL);
        LENGTH_FINGERPRINTS.put(800, 0x5f2f123bfc815f81L);
        LENGTH_FINGERPRINTS.put(640, 0x6396fc6a67293cf4L);
        LENGTH_FINGERPRINTS.put(512, 0x45c01b4934ddbbbeL);
        LENGTH_FINGERPRINTS.put(409, 0xfcd19b617551db45L);
        LENGTH_FINGERPRINTS.put(327, 0x4eee69e12854871eL);
        LENGTH_FINGERPRINTS.put(261, 0xab753446a3bbd532L);
        LENGTH_FINGERPRINTS.put(208, 0x54242fe06a291c3fL);
        LENGTH_FINGERPRINTS.put(166, 0x4f7acff7703a635bL);
        LENGTH_FINGERPRINTS.put(132, 0xa784bd0a1f22cc7fL);
        LENGTH_FINGERPRINTS.put(105, 0xf19118e187456638L);
        LENGTH_FINGERPRINTS.put(84, 0x3e2e58f9196abfe5L);
        LENGTH_FINGERPRINTS.put(67, 0xd38ae3dec0107aeaL);
        LENGTH_FINGERPRINTS.put(53, 0xea3033885868e10eL);
        LENGTH_FINGERPRINTS.put(42, 0x1394a146d0d7e04bL);
        LENGTH_FINGERPRINTS.put(33, 0x9962499315d2e8daL);
        LENGTH_FINGERPRINTS.put(26, 0x0849f5cfa85489b5L);
        LENGTH_FINGERPRINTS.put(20, 0x83b395ff19bf2171L);
        LENGTH_FINGERPRINTS.put(16, 0x9d33dd141bd55d9aL);
        LENGTH_FINGERPRINTS.put(12, 0x196248eb0b02466aL);
        LENGTH_FINGERPRINTS.put(9, 0x1cf73a50ff120336L);
        LENGTH_FINGERPRINTS.put(7, 0xb451c339457dbf51L);
        LENGTH_FINGERPRINTS.put(5, 0x681982c5e7b74064L);
        LENGTH_FINGERPRINTS.put(4, 0xc5ce47450ca6c021L);
        LENGTH_FINGERPRINTS.put(3, 0x9fcc3c3fde4d5ff7L);
        LENGTH_FINGERPRINTS.put(2, 0x090966a836e5fa4bL);
        LENGTH_FINGERPRINTS.put(1, 0x8199675ecaa6fe64L);
        LENGTH_FINGERPRINTS.put(0, 0x23ad7c904aa665e3L);
//                    .build();
    }
    private static final HashFunction HASH_FN = Hashing.fingerprint2011();

    public static String repeat(String string, int count) {
        assert string != null;

        assert count > 1;

        // IF YOU MODIFY THE CODE HERE, you must update StringsRepeatBenchmark
        final int len = string.length();
        final long longSize = (long) len * (long) count;
        final int size = (int) longSize;
        if (size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
        }

        final char[] array = new char[size];
        string.getChars(0, len, array, 0);
        int n;
        for (n = len; n < size - n; n <<= 1) {
            System.arraycopy(array, 0, array, n, n);
        }
        System.arraycopy(array, 0, array, n, size - n);
        return new String(array);
    }

    // If this test fails, all bets are off
    @Test public void testReallySimpleFingerprints() {
        Assertions.assertEquals(8473225671271759044L, fingerprint("test".getBytes(UTF_8)));
        // 32 characters long
//        Assertions.assertEquals(7345148637025587076L, fingerprint(Strings.repeat("test", 8).getBytes(UTF_8)));
        Assertions.assertEquals(7345148637025587076L, fingerprint(repeat("test", 8).getBytes(UTF_8)));
        // 256 characters long
        Assertions.assertEquals(4904844928629814570L, fingerprint(repeat("test", 64).getBytes(UTF_8)));
    }

    @Test public void testStringsConsistency() {
        for (String s : Arrays.asList("", "some", "test", "strings", "to", "try")) {
            Assertions.assertEquals(HASH_FN.newHasher().putUnencodedChars(s).hash(), HASH_FN.hashUnencodedChars(s));
        }
    }

    @Test public void testUtf8() {
        char[] charsA = new char[128];
        char[] charsB = new char[128];

        for (int i = 0; i < charsA.length; i++) {
            if (i < 100) {
                charsA[i] = 'a';
                charsB[i] = 'a';
            } else {
                // Both two-byte characters, but must be different
                charsA[i] = (char) (0x0180 + i);
                charsB[i] = (char) (0x0280 + i);
            }
        }

        String stringA = new String(charsA);
        String stringB = new String(charsB);
//        assertThat(stringA).isNotEqualTo(stringB);
        Assertions.assertNotEquals(stringA, stringB);
//        assertThat(HASH_FN.hashUnencodedChars(stringA))
//                .isNotEqualTo(HASH_FN.hashUnencodedChars(stringB));
        Assertions.assertNotEquals(HASH_FN.hashUnencodedChars(stringA), HASH_FN.hashUnencodedChars(stringB));
//        assertThat(fingerprint(stringA.getBytes(UTF_8)))
//                .isNotEqualTo(fingerprint(stringB.getBytes(UTF_8)));
        Assertions.assertNotEquals(fingerprint(stringA.getBytes(UTF_8)), fingerprint(stringB.getBytes(UTF_8)));

        // ISO 8859-1 only has 0-255 (ubyte) representation so throws away UTF-8 characters
        // greater than 127 (ie with their top bit set).
        // Don't attempt to do this in real code.
        Assertions.assertEquals(
                fingerprint(stringA.getBytes(ISO_8859_1)), fingerprint(stringB.getBytes(ISO_8859_1)));
    }

    @Test public void testMumurHash64() {
        byte[] bytes = "test".getBytes(UTF_8);
        Assertions.assertEquals(
                1618900948208871284L, Fingerprint2011.murmurHash64WithSeed(bytes, 0, bytes.length, 1));

        bytes = "test test test".getBytes(UTF_8);
        Assertions.assertEquals(
                1231316968406779356L * 10, // UnsignedLong.valueOf("12313169684067793560").longValue(),
                Fingerprint2011.murmurHash64WithSeed(bytes, 0, bytes.length, 1));
    }

    @Test public void testPutNonChars() {
        Hasher hasher = HASH_FN.newHasher();
        // Expected data is 0x0100010100000000
        hasher
                .putBoolean(true)
                .putBoolean(true)
                .putBoolean(false)
                .putBoolean(true)
                .putBoolean(false)
                .putBoolean(false)
                .putBoolean(false)
                .putBoolean(false);
        final long hashCode = hasher.hash().asLong();

        hasher = HASH_FN.newHasher();
        hasher
                .putByte((byte) 0x01)
                .putByte((byte) 0x01)
                .putByte((byte) 0x00)
                .putByte((byte) 0x01)
                .putByte((byte) 0x00)
                .putByte((byte) 0x00)
                .putByte((byte) 0x00)
                .putByte((byte) 0x00);
        Assertions.assertEquals(hashCode, hasher.hash().asLong());

        hasher = HASH_FN.newHasher();
        hasher
                .putChar((char) 0x0101)
                .putChar((char) 0x0100)
                .putChar((char) 0x0000)
                .putChar((char) 0x0000);
        Assertions.assertEquals(hashCode, hasher.hash().asLong());

        hasher = HASH_FN.newHasher();
        hasher.putBytes(new byte[] {0x01, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00});
        Assertions.assertEquals(hashCode, hasher.hash().asLong());

        hasher = HASH_FN.newHasher();
        hasher.putLong(0x0000000001000101L);
        Assertions.assertEquals(hashCode, hasher.hash().asLong());

        hasher = HASH_FN.newHasher();
        hasher
                .putShort((short) 0x0101)
                .putShort((short) 0x0100)
                .putShort((short) 0x0000)
                .putShort((short) 0x0000);
        Assertions.assertEquals(hashCode, hasher.hash().asLong());
    }

    @Test public void testHashFloatIsStable() {
        // This is about the best we can do for floating-point
        Hasher hasher = HASH_FN.newHasher();
        hasher.putFloat(0x01000101f).putFloat(0f);
        Assertions.assertEquals(0x96a4f8cc6ecbf16L, hasher.hash().asLong());

        hasher = HASH_FN.newHasher();
        hasher.putDouble(0x0000000001000101d);
        Assertions.assertEquals(0xcf54171253fdc198L, hasher.hash().asLong());
    }

    /** Convenience method to compute a fingerprint on a full bytes array. */
    private static long fingerprint(byte[] bytes) {
        return fingerprint(bytes, bytes.length);
    }

    /** Convenience method to compute a fingerprint on a subset of a byte array. */
    private static long fingerprint(byte[] bytes, int length) {
        return HASH_FN.hashBytes(bytes, 0, length).asLong();
    }

    /**
     * Tests that the Java port of Fingerprint2011 provides the same results on buffers up to 800
     * bytes long as the original implementation in C++. See http://cl/106539598
     */
    @Test public void testMultipleLengths() {
        int iterations = 800;
        byte[] buf = new byte[iterations * 4];
        int bufLen = 0;
        long h = 0;
        for (int i = 0; i < iterations; ++i) {
            h ^= fingerprint(buf, i);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, i * i % bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, i * i * i % bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            int x0 = buf[bufLen - 1] & 0xff;
            int x1 = buf[bufLen - 2] & 0xff;
            int x2 = buf[bufLen - 3] & 0xff;
            int x3 = buf[bufLen / 2] & 0xff;
            buf[((x0 << 16) + (x1 << 8) + x2) % bufLen] ^= x3;
            buf[((x1 << 16) + (x2 << 8) + x3) % bufLen] ^= i % 256;
        }
        Assertions.assertEquals(0xeaa3b1c985261632L, h);
    }

    private static long remix(long h) {
        h ^= h >>> 41;
        h *= 949921979;
        return h;
    }

    private static byte getChar(long h) {
        return (byte) ('a' + ((h & 0xfffff) % 26));
    }
}