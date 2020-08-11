package kaptainwutax.mathutils.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongUnaryOperator;

public class Bits {

    public static boolean isPowerOf2(long value) {
        return (value & -value) == value;
    }

    public static long getPow2(int bits) {
        return 1L << bits;
    }

    public static long getMask(int bits) {
        return bits >= 64 ? ~0 : getPow2(bits) - 1;
    }

    public static BigInteger getBigMask(int bits) {
        return BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE);
    }

    public static long mask(long value, int bits) {
        return value & getMask(bits);
    }

    public static BigInteger bigMask(BigInteger value, int bits) {
        return value.and(getBigMask(bits));
    }

    public static long maskSigned(long value, int bits) {
        return value << (64 - bits) >> (64 - bits); //removes top bits and copies sign bits back down
    }

    public static long modInverse(long a) {
        return modInverse(a, 64);
    }

    public static long modInverse(long a, int k) {
        long x = ((((a << 1) ^ a) & 4) << 1) ^ a;

        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;

        return mask(x, k);
    }

    public static List<Long> lift(long value, int bit, long target, int bits, int offset, LongUnaryOperator hash) {
        return lift(value, bit, target, bits, offset, hash, new ArrayList<>());
    }

    public static <T extends Collection<Long>> T lift(long value, int bit, long target, int bits, int offset,
                                                      LongUnaryOperator hash, T result) {
        if(bit >= bits) {
            if(mask(target, bit + offset) == mask(hash.applyAsLong(value), bit + offset)) {
                result.add(value);
            }
        } else if(mask(target, bit) == mask(hash.applyAsLong(value), bit)) {
            lift(value, bit + 1, target, bits, offset, hash, result);
            lift(value | getPow2(bit + offset), bit + 1, target, bits, offset, hash, result);
        }

        return result;
    }

}
