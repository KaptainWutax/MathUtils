package kaptainwutax.mathutils.util;

import java.math.BigInteger;
import java.util.function.Predicate;

public final class Combinatorics {

    public static final int MAX_LONG_FACTORIAL = 20;
    private static final long[] FACTORIAL = new long[MAX_LONG_FACTORIAL + 1];

    static {
        FACTORIAL[0] = 1;

        for(int i = 1; i < FACTORIAL.length; i++) {
            FACTORIAL[i] = FACTORIAL[i - 1] * i;
        }
    }

    public static long getFactorial(int n) {
        return FACTORIAL[n];
    }

    public static BigInteger getBigFactorial(int n) {
        if(n <= MAX_LONG_FACTORIAL) {
            return BigInteger.valueOf(getFactorial(n));
        }

        BigInteger result = BigInteger.valueOf(getFactorial(MAX_LONG_FACTORIAL));

        for(int i = MAX_LONG_FACTORIAL; i < n; i++) {
            result = result.multiply(BigInteger.valueOf(i + 1));
        }

        return result;
    }

    public static long getPermutations(int n, int r) {
        return getFactorial(n) / getFactorial(n - r);
    }

    public static BigInteger getBigPermutations(int n, int r) {
        return getBigFactorial(n).divide(getBigFactorial(n - r));
    }

    public static long getCombinations(int n, int r) {
        return getPermutations(n, r) / getFactorial(r);
    }

    public static BigInteger getBigCombinations(int n, int r) {
        return getBigPermutations(n, r).divide(getBigFactorial(r));
    }

    public static void permute(int n, int r, Predicate<int[]> shouldContinue) {
        if(n > MAX_LONG_FACTORIAL)bigPermute(n, r, shouldContinue);
        else smallPermute(n, r, shouldContinue);
    }

    private static void smallPermute(int n, int r, Predicate<int[]> shouldContinue) {
        long max = getFactorial(n), increment = getFactorial(n - r);

        for(long perm = 0; perm < max; perm += increment) {
            long permCopy = perm;
            int[] indices = new int[n];

            for(int i = 0; i < n; i++) {
                indices[i] = (int)(permCopy / getFactorial(n - 1 - i));
                permCopy -= indices[i] * getFactorial(n - 1 - i);
            }

            if(!acceptPermutation(n, r, indices, shouldContinue))return;
        }
    }

    private static void bigPermute(int n, int r, Predicate<int[]> shouldContinue) {
        BigInteger max = getBigFactorial(n), increment = getBigFactorial(n - r);

        for(BigInteger perm = BigInteger.ZERO; perm.compareTo(max) < 0; perm = perm.add(increment)) {
            BigInteger permCopy = perm;
            int[] indices = new int[n];

            for(int i = 0; i < n; i++) {
                BigInteger f = getBigFactorial(n - 1 - i);
                indices[i] = permCopy.divide(f).intValue();
                permCopy = permCopy.subtract(f.multiply(BigInteger.valueOf(indices[i])));
            }

            if(!acceptPermutation(n, r, indices, shouldContinue))return;
        }
    }

    private static boolean acceptPermutation(int n, int r, int[] indices, Predicate<int[]> shouldContinue) {
        int[] permutation = new int[n];

        for(int i = 0; i < r; i++) {
            int wantedIndex = indices[i];
            int currentIndex = 0;

            for(int j = 0; j < permutation.length; j++) {
                if(permutation[j] != 0)continue;
                if(currentIndex++ == wantedIndex) {
                    currentIndex = j;
                    break;
                }
            }

            permutation[currentIndex] = i + 1;
        }

        return shouldContinue.test(permutation);
    }

    public static void combine(int n, int r, Predicate<int[]> shouldContinue) {
        int[] combination = new int[r];

        for(int i = 0; i < r; i++)combination[i] = i;

        while(combination[r - 1] < n) {
            if(!shouldContinue.test(combination))return;
            int t = r - 1;
            while(t != 0 && combination[t] == n - r + t)t--;
            combination[t]++;

            for(int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }
    }

}
