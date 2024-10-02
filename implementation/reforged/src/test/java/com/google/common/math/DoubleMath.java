package com.google.common.math;

import java.math.RoundingMode;

public class DoubleMath {

    private static final double MIN_LONG_AS_DOUBLE = -0x1p63;
    /*
     * We cannot store Long.MAX_VALUE as a double without losing precision. Instead, we store
     * Long.MAX_VALUE + 1 == -Long.MIN_VALUE, and then offset all comparisons by 1.
     */
    private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 0x1p63;

    static double roundIntermediate(double x, RoundingMode mode) {
        if (!DoubleUtils.isFinite(x)) {
            throw new ArithmeticException("input is infinite or NaN");
        }
        switch (mode) {
            case UNNECESSARY:
                MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(x));
                return x;

            case FLOOR:
                if (x >= 0.0 || isMathematicalInteger(x)) {
                    return x;
                } else {
                    return (long) x - 1;
                }

            case CEILING:
                if (x <= 0.0 || isMathematicalInteger(x)) {
                    return x;
                } else {
                    return (long) x + 1;
                }

            case DOWN:
                return x;

            case UP:
                if (isMathematicalInteger(x)) {
                    return x;
                } else {
                    return (long) x + (x > 0 ? 1 : -1);
                }

            case HALF_EVEN:
                return Math.rint(x);

            case HALF_UP:
            {
                double z = Math.rint(x);
                if (Math.abs(x - z) == 0.5) {
                    return x + Math.copySign(0.5, x);
                } else {
                    return z;
                }
            }

            case HALF_DOWN:
            {
                double z = Math.rint(x);
                if (Math.abs(x - z) == 0.5) {
                    return x;
                } else {
                    return z;
                }
            }

            default:
                throw new AssertionError();
        }
    }

    public static long roundToLong(double x, RoundingMode mode) {
        double z = roundIntermediate(x, mode);
        MathPreconditions.checkInRangeForRoundingInputs(
                MIN_LONG_AS_DOUBLE - z < 1.0 & z < MAX_LONG_AS_DOUBLE_PLUS_ONE, x, mode);
        return (long) z;
    }

    public static boolean isMathematicalInteger(double x) {
        return DoubleUtils.isFinite(x)
                && (x == 0.0
                || DoubleUtils.SIGNIFICAND_BITS - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(x)) <= Math.getExponent(x));
    }
}
