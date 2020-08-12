package kaptainwutax.mathutils.solver;

import kaptainwutax.mathutils.util.Mth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.LongUnaryOperator;

public class Hensel {

    public static List<Long> simpleLift(long target, int bits, LongUnaryOperator hash) {
        return lift(0L, 0, target, bits, 0, hash);
    }

    public static <T extends Collection<Long>> T simpleLift(long target, int bits, LongUnaryOperator hash, T result) {
        return lift(0L, 0, target, bits, 0, hash, result);
    }

    public static List<Long> lift(long value, int bit, long target, int bits, int offset, LongUnaryOperator hash) {
        return lift(value, bit, target, bits, offset, hash, new ArrayList<>());
    }

    public static <T extends Collection<Long>> T lift(long value, int bit, long target, int bits, int offset,
                                                      LongUnaryOperator hash, T result) {
        if(bit >= bits) {
            if(Mth.mask(target, bit + offset) == Mth.mask(hash.applyAsLong(value), bit + offset)) {
                result.add(value);
            }
        } else if(Mth.mask(target, bit) == Mth.mask(hash.applyAsLong(value), bit)) {
            lift(value, bit + 1, target, bits, offset, hash, result);
            lift(value | Mth.getPow2(bit + offset), bit + 1, target, bits, offset, hash, result);
        }

        return result;
    }

}
