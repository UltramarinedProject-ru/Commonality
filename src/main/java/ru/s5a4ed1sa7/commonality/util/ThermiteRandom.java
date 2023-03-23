package ru.s5a4ed1sa7.commonality.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ThermiteRandom extends Random {

    private static final long serialVersionUID = 6208727693524452904L;
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)
    boolean haveNextNextGaussian = false;
    double nextNextGaussian = 0;
    private long seed;

    public ThermiteRandom() {
        this(seedUniquifier() ^ System.nanoTime());
    }

    public ThermiteRandom(long seed) {
        this.seed = seed;
    }

    private static long seedUniquifier() {
        for (; ; ) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public boolean nextBoolean() {
        return next(1) != 0;
    }

    public double nextDouble() {
        return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
    }

    public synchronized long getSeed() {
        return seed;
    }

    public synchronized void setSeed(long seed) {
        this.seed = seed;
    }


    public ThermiteRandom clone() {
        return new ThermiteRandom(getSeed());
    }

    public int next(int nbits) {
        long x = seed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        seed = x;
        x &= ((1L << nbits) - 1);
        return (int) x;
    }

    synchronized public double nextGaussian() {
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        } else {
            double v1, v2, s;
            do {
                v1 = 2 * nextDouble() - 1;
                v2 = 2 * nextDouble() - 1;
                s = v1 * v1 + v2 * v2;
            } while (s >= 1 || s == 0);
            double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
            nextNextGaussian = v2 * multiplier;
            haveNextNextGaussian = true;
            return v1 * multiplier;
        }
    }

    public int nextInt(int bound) {
        if (bound < 0) {
            throw new RuntimeException("BadBound");
        }

        int r = next(31);
        int m = bound - 1;
        if ((bound & m) == 0)
        {
            r = (int) ((bound * (long) r) >> 31);
        } else {
            for (int u = r;
                 u - (r = u % bound) + m < 0;
                 u = next(31))
                ;
        }
        return r;
    }

    public int nextInt() {
        return next(32);
    }
}
