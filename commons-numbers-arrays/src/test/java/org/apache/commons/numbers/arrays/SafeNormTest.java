/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.numbers.arrays;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link SafeNorm} class.
 */
class SafeNormTest {

    private static final double MIN_UNSCALED_VECTOR_VALUE = 1e-100;

    @Test
    void testTiny() {
        final double s = 1e-320;

        final double[] v2 = new double[] {s, s};
        Assertions.assertEquals(Math.sqrt(2) * s, SafeNorm.value(v2));

        final double[] v3 = new double[] {s, s, s};
        Assertions.assertEquals(Math.sqrt(3) * s, SafeNorm.value(v3));
        Assertions.assertEquals(Math.sqrt(3) * s, SafeNorm.value(s, s, s));
    }

    @Test
    void testBig() {
        final double s = 1e300;

        final double[] v2 = new double[] {s, s};
        Assertions.assertEquals(Math.sqrt(2) * s, SafeNorm.value(v2));

        final double[] v3 = new double[] {s, s, s};
        Assertions.assertEquals(Math.sqrt(3) * s, SafeNorm.value(v3));
        Assertions.assertEquals(Math.sqrt(3) * s, SafeNorm.value(s, s, s));
    }

    @Test
    void testOne3D() {
        final double s = 1;
        final double[] v = new double[] {s, s, s};
        Assertions.assertEquals(Math.sqrt(3), SafeNorm.value(v));
    }

    @Test
    void testUnit3D() {
        Assertions.assertEquals(1, SafeNorm.value(new double[] {1, 0, 0}));
        Assertions.assertEquals(1, SafeNorm.value(new double[] {0, 1, 0}));
        Assertions.assertEquals(1, SafeNorm.value(new double[] {0, 0, 1}));

        Assertions.assertEquals(1, SafeNorm.value(1, 0, 0));
        Assertions.assertEquals(1, SafeNorm.value(0, 1, 0));
        Assertions.assertEquals(1, SafeNorm.value(0, 0, 1));
    }

    @Test
    void testSimple() {
        final double[] v = new double[] {-0.9, 8.7, -6.5, -4.3, -2.1, 0, 1.2, 3.4, -5.6, 7.8, 9.0};
        final double expected = directNorm(v);
        Assertions.assertEquals(expected, SafeNorm.value(v));
    }

    @Test
    void testZero() {
        final double[] v = new double[] {0, 0, 0, 0, 0};
        Assertions.assertEquals(0d, SafeNorm.value(v));
    }

    @Test
    void testTinyAndSmallNormal() {
        // Ensure the sum of the squared values for 'normal' values (1e-19*1e-19)
        // is less than largest tiny value (1e-20)
        final double[] v = new double[] {1e-20, 1e-19};
        Assertions.assertEquals(Math.sqrt(101) * 1e-20, SafeNorm.value(v));
    }

    @Test
    void testRandom3D() {
        // arrange
        final VectorNormTestGenerator gen = new VectorNormTestGenerator(new Random(1L), 3);
        final int cnt = 1000;

        // act/assert
        for (int i = 0; i < cnt; ++i) {
            gen.next();

            checkNorm(gen.getVector(), gen.getExpectedNorm());
        }
    }

    private static void checkNorm(final double[] vec, final double expected) {
        final double direct = directNorm(vec);
        final double safe = SafeNorm.value(vec);

        Assertions.assertTrue(Double.isFinite(safe));

        Assertions.assertTrue(Math.abs(safe - expected) <= Math.abs(direct - expected),
                () -> "Expected SafeNorm to produce closer result to expected value than direct computation; " +
                "input vec= " + Arrays.toString(vec) + ", exact result= " + expected + ", direct result= " + direct +
                ", safe result= " + safe);

        if (vec.length == 3) {
            Assertions.assertEquals(safe, SafeNorm.value(vec[0], vec[1], vec[2]));
        }
    }


    private static final class VectorNormTestGenerator {

        private final Random random;

        private final int len;

        private double[] vector;

        private double expectedNorm;

        VectorNormTestGenerator(final Random random, final int len) {
            this.random = random;
            this.len = len;
        }

        public double[] getVector() {
            return vector;
        }

        public double getExpectedNorm() {
            return expectedNorm;
        }

        public void next() {
            vector = new double[len];

            // populate with values in the range [0, 1]
            for (int i = 0; i < vector.length; ++i) {
                vector[i] = Math.max(random.nextDouble(), MIN_UNSCALED_VECTOR_VALUE);
            }

            // compute the norm in quad precision
            expectedNorm = quadNorm(vector);
        }
    }

    @Test
    public void testConsistency3D() {
        // arrange
        final Random rnd = new Random(1L);
        final Supplier<double[]> gen = vectorGenerator(3, rnd);
        final int cnt = 100;
        final double acc = 1e-3;

        for (int i = 0; i < cnt; ++i) {
            final double[] vec = gen.get();

            final double exact = quadNorm(vec);
            final double direct = directNorm(vec);

            // act
            final double safe = SafeNorm.value(vec[0], vec[1], vec[2]);

            // assert
            Assertions.assertTrue(Double.isFinite(safe));

            Assertions.assertTrue(Math.abs(safe - exact) <= Math.abs(direct - exact),
                    () -> "Expected SafeNorm to produce closer result to exact value than direct computation; " +
                    "input vec= " + Arrays.toString(vec) + ", exact result= " + exact + ", direct result= " + direct +
                    ", safe result= " + safe);
        }
    }

    /**
     * Direct computation.
     *
     * @param v Array.
     * @return the norm using direct summation.
     */
    private static double directNorm(final double[] v) {
        double n = 0;
        for (int i = 0; i < v.length; i++) {
            n += v[i] * v[i];
        }
        return Math.sqrt(n);
    }

    private static double quadNorm(final double[] v) {
        // compute the sum of squares
        final MathContext ctx = MathContext.DECIMAL128;

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal n;
        for (int i = 0; i < v.length; ++i) {
            n = BigDecimal.valueOf(v[i]);
            sum = sum.add(n.multiply(n,ctx), ctx);
        }

        return sqrt(sum, ctx).doubleValue();
    }

    private static BigDecimal sqrt(final BigDecimal n, final MathContext ctx) {
        final BigDecimal two = BigDecimal.valueOf(2);

        BigDecimal x = n.divide(two, ctx);
        BigDecimal sq = x.multiply(x, ctx);

        BigDecimal f = sq.subtract(n, ctx);
        BigDecimal fPrime;
        BigDecimal fPrevAbs = null;

        while (fPrevAbs == null || f.abs().compareTo(fPrevAbs) < 0) {
            System.out.println("\nf       = " + f);
            System.out.println("fPrevAbs= " + fPrevAbs);

            fPrime = x.multiply(two, ctx);

            x = x.subtract(f.divide(fPrime, ctx), ctx);

            sq = x.multiply(x, ctx);

            fPrevAbs = f.abs();
            f = sq.subtract(n, ctx);
        }

        System.out.println("\nDone:\nf       = " + f);
        System.out.println("fPrevAbs= " + fPrevAbs);
        System.out.println("diff    = " + n.subtract(x.multiply(x)));

        return x;
    }

    private static Supplier<double[]> vectorGenerator(final int len, final Random rnd) {
        return () -> {
            final double[] arr = new double[len];

            double sz;
            int minExp;
            int maxExp;
            for (int i = 0; i < len; ++i) {
                sz = rnd.nextDouble();

                if (sz < 0.3) {
                    minExp = Double.MIN_EXPONENT;
                    maxExp = -20;
                } else if (sz > 0.7) {
                    minExp = +19;
                    maxExp = Double.MAX_EXPONENT;
                } else {
                    minExp = -20;
                    maxExp = +19;
                }

                arr[i] = randomDouble(minExp, maxExp, rnd);
            }

            System.out.println(Arrays.toString(arr));

            return arr;
        };
    }

    private static double randomDouble(final int minExp, final int maxExp, final Random rnd) {
        // Create random doubles using random bits in the sign bit and the mantissa.
        // Then create an exponent in the range -64 to 64. Thus the sum product
        // of 4 max or min values will not over or underflow.
        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rnd.nextLong() & mask;
        // The exponent must be unsigned so + 1023 to the signed exponent
        final int expRange = Math.abs(maxExp - minExp);
        final long exp = rnd.nextInt(expRange) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }
}
