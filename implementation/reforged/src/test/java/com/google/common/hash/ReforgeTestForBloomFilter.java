package com.google.common.hash;

import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.RoundingMode;

public class ReforgeTestForBloomFilter {

    @ParameterizedTest
    @ValueSource(doubles = {1.501, 14.49901, 200.01, 99.998})
    public void test_DoubleMath_roundToLong(double x) {
        double z = Math.rint(x);
        z = Math.abs(x - z) == 0.5 ? (x + Math.copySign(0.5, x)) : z;
        if (!(-0x1p63 - z < 1.0 & z < 0x1p63)) {
            throw new ArithmeticException("rounded value is out of range for input " + z + " and rounding mode HALF_UP");
        }
        Assertions.assertEquals((long) z, DoubleMath.roundToLong(x, RoundingMode.HALF_UP));
        System.out.println((long) z);
    }

    @ParameterizedTest
    @ValueSource(ints = {-10086, 110119120})
    public void test_LongMath_CheckedMultiply(int dataLength) {
        Assertions.assertEquals(LongMath.checkedMultiply(dataLength, 64L), dataLength * 64L);;
    }
}
