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
import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NormsTest {

    private static final int RAND_VECTOR_CNT = 1_000;

    private static final int MAX_ULP_ERR = 2;

    @Test
    void testManhattan_1d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.manhattan(0d));
        Assertions.assertEquals(0d, Norms.manhattan(-0d));
        Assertions.assertEquals(1d, Norms.manhattan(1d));
        Assertions.assertEquals(1d, Norms.manhattan(-1d));

        Assertions.assertEquals(Double.NaN, Norms.manhattan(Double.NaN));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.manhattan(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.manhattan(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testManhattan_2d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.manhattan(0d, -0d));
        Assertions.assertEquals(3d, Norms.manhattan(-1d, 2d));

        Assertions.assertEquals(Double.NaN, Norms.manhattan(Double.NaN, 1d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.manhattan(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    void testManhattan_3d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.manhattan(0d, -0d, 0d));
        Assertions.assertEquals(6d, Norms.manhattan(-1d, 2d, -3d));

        Assertions.assertEquals(Double.NaN, Norms.manhattan(-2d, Double.NaN, 1d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.manhattan(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -4d));
    }

    @Test
    void testManhattan_array() {
        // act/assert
        Assertions.assertEquals(0d, Norms.manhattan(new double[0]));
        Assertions.assertEquals(0d, Norms.manhattan(new double[] {0d, -0d}));
        Assertions.assertEquals(6d, Norms.manhattan(new double[] {-1d, 2d, -3d}));
        Assertions.assertEquals(10d, Norms.manhattan(new double[] {-1d, 2d, -3d, 4d}));

        Assertions.assertEquals(Double.NaN, Norms.manhattan(new double[] {-2d, Double.NaN, 1d}));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.manhattan(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));
    }

    @Test
    void testEuclidean_1d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.euclidean(0d));
        Assertions.assertEquals(0d, Norms.euclidean(-0d));
        Assertions.assertEquals(1d, Norms.euclidean(1d));
        Assertions.assertEquals(1d, Norms.euclidean(-1d));

        Assertions.assertEquals(Double.NaN, Norms.euclidean(Double.NaN));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.euclidean(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.euclidean(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testEuclidean_2d_random() {
        // arrange
        final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP, 1L);

        // act/assert
        checkEuclideanRandom(2, rng, v -> Norms.euclidean(v[0], v[1]));
    }

    @Test
    void testEuclidean_3d_random() {
        // arrange
        final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP, 1L);

        // act/assert
        checkEuclideanRandom(3, rng, v -> Norms.euclidean(v[0], v[1], v[2]));
    }

    @Test
    void testEuclidean_array_simple() {
        // act/assert
        Assertions.assertEquals(0d, Norms.euclidean(new double[0]));
        Assertions.assertEquals(5d, Norms.euclidean(new double[] {-3d, 4d}));

        Assertions.assertEquals(Math.sqrt(2), Norms.euclidean(new double[] {1d, -1d}));
        Assertions.assertEquals(Math.sqrt(3), Norms.euclidean(new double[] {1d, -1d, 1d}));
        Assertions.assertEquals(2, Norms.euclidean(new double[] {1d, -1d, 1d, -1d}));

        final double[] longVec = new double[] {-0.9, 8.7, -6.5, -4.3, -2.1, 0, 1.2, 3.4, -5.6, 7.8, 9.0};
        Assertions.assertEquals(directEuclideanNorm(longVec), Norms.euclidean(longVec));

        Assertions.assertEquals(Double.NaN, Norms.euclidean(new double[] {-2d, Double.NaN, 1d}));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.euclidean(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));
    }

    @Test
    void testEuclidean_array_edgeCases() {
        // act/assert
        checkScaledEuclideanNorm(new double[] {1, 1, 1}, 0x1.0p100, Norms::euclidean);
    }

    @Test
    void testEuclidean_array_random() {
        // arrange
        final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP, 1L);

        // act/assert
        checkEuclideanRandom(2, rng, Norms::euclidean);
        checkEuclideanRandom(3, rng, Norms::euclidean);
        checkEuclideanRandom(4, rng, Norms::euclidean);
        checkEuclideanRandom(10, rng, Norms::euclidean);
    }

    @Test
    void testMaximum_1d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.maximum(0d));
        Assertions.assertEquals(0d, Norms.maximum(-0d));
        Assertions.assertEquals(1d, Norms.maximum(1d));
        Assertions.assertEquals(1d, Norms.maximum(-1d));

        Assertions.assertEquals(Double.NaN, Norms.maximum(Double.NaN));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.NEGATIVE_INFINITY));
    }

    @Test
    void testMaximum_2d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.maximum(0d, -0d));
        Assertions.assertEquals(2d, Norms.maximum(1d, -2d));
        Assertions.assertEquals(3d, Norms.maximum(3d, 1d));

        Assertions.assertEquals(Double.NaN, Norms.maximum(Double.NaN, 0d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.POSITIVE_INFINITY, 0d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.NEGATIVE_INFINITY, 0d));
    }

    @Test
    void testMaximum_3d() {
        // act/assert
        Assertions.assertEquals(0d, Norms.maximum(0d, -0d, 0d));
        Assertions.assertEquals(3d, Norms.maximum(1d, -2d, 3d));
        Assertions.assertEquals(4d, Norms.maximum(-4d, -2d, 3d));

        Assertions.assertEquals(Double.NaN, Norms.maximum(3d, Double.NaN, 0d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.POSITIVE_INFINITY, 0d, 1d));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Norms.maximum(Double.NEGATIVE_INFINITY, 0d, -1d));
    }

    @Test
    void testMaximum_array() {
        // act/assert
        Assertions.assertEquals(0d, Norms.maximum(new double[0]));
        Assertions.assertEquals(0d, Norms.maximum(new double[] {0d, -0d}));
        Assertions.assertEquals(3d, Norms.maximum(new double[] {-1d, 2d, -3d}));
        Assertions.assertEquals(4d, Norms.maximum(new double[] {-1d, 2d, -3d, 4d}));

        Assertions.assertEquals(Double.NaN, Norms.maximum(new double[] {-2d, Double.NaN, 1d}));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.maximum(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));
    }

    /** Check a number of random vectors of length {@code len} with various exponent
     * ranges.
     * @param len vector array length
     * @param rng random number generator
     * @param fn euclidean norm test function
     */
    private static void checkEuclideanRandom(final int len, final UniformRandomProvider rng,
            final ToDoubleFunction<double[]> fn) {
        checkEuclideanRandom(len, +600, +620, rng, fn);
        checkEuclideanRandom(len, +490, +510, rng, fn);
        checkEuclideanRandom(len, +400, +420, rng, fn);
        checkEuclideanRandom(len, +100, +120, rng, fn);
        checkEuclideanRandom(len, -10, +10, rng, fn);
        checkEuclideanRandom(len, -120, -100, rng, fn);
        checkEuclideanRandom(len, -420, -400, rng, fn);
        checkEuclideanRandom(len, -510, -490, rng, fn);
        checkEuclideanRandom(len, -620, -600, rng, fn);

        checkEuclideanRandom(len, -600, +600, rng, fn);
    }

    /** Check a number of random vectors of length {@code len} with elements containing
     * exponents in the range {@code [minExp, maxExp]}.
     * @param len vector array length
     * @param minExp min exponent
     * @param maxExp max exponent
     * @param rng random number generator
     * @param fn euclidean norm test function
     */
    private static void checkEuclideanRandom(final int len, final int minExp, final int maxExp,
            final UniformRandomProvider rng, final ToDoubleFunction<double[]> fn) {
        for (int i = 0; i < RAND_VECTOR_CNT; ++i) {
            // arrange
            final double[] v = randomVector(len, minExp, maxExp, rng);

            final double exact = exactEuclideanNorm(v);
            final double direct = directEuclideanNorm(v);

            // act
            final double actual = fn.applyAsDouble(v);

            // assert
            Assertions.assertTrue(Double.isFinite(actual),() ->
                "Computed norm was not finite; vector= " + Arrays.toString(v) + ", exact= " + exact +
                ", direct= " + direct + ", actual= " + actual);

            if (Double.isFinite(direct) && direct != 0d) {
                final int directUlpError = Math.abs(computeUlpDifference(exact, direct));
                final int actualUlpError = Math.abs(computeUlpDifference(exact, actual));

                Assertions.assertTrue(actualUlpError <= directUlpError || (actualUlpError <= MAX_ULP_ERR), () ->
                    "Computed norm error exceeds bounds; vector= " + Arrays.toString(v) +
                    ", exact= " + exact + ", direct= " + direct + ", directUlpError= " + directUlpError +
                    ", actual= " + actual + ", actualUlpError= " + actualUlpError);
            }
        }
    }

    /** Assert that {@code |scale * v| = scale * |v|}.
     * @param v unscaled vector
     * @param scale scaled factor
     * @param fn euclidean norm function
     */
    private static void checkScaledEuclideanNorm(final double[] v, final double scale,
            final ToDoubleFunction<double[]> fn) {
        final double norm = directEuclideanNorm(v) * scale;

        final double[] scaledV = new double[v.length];
        for (int i = 0; i < v.length; ++i) {
            scaledV[i] = v[i] * scale;
        }

        Assertions.assertEquals(norm * scale * v.length, fn.applyAsDouble(scaledV));
    }

   /** Direct euclidean norm computation.
    * @param v array
    * @return euclidean norm using direct summation.
    */
   private static double directEuclideanNorm(final double[] v) {
       double n = 0;
       for (int i = 0; i < v.length; i++) {
           n += v[i] * v[i];
       }
       return Math.sqrt(n);
   }

   /** Compute the exact double value of the vector norm using BigDecimals
    * with a math context of {@link MathContext#DECIMAL128}.
    * @param v array
    * @return euclidean norm using BigDecimal with MathContext.DECIMAL128
    */
   private static double exactEuclideanNorm(final double[] v) {
       final MathContext ctx = MathContext.DECIMAL128;

       BigDecimal sum = BigDecimal.ZERO;
       for (final double d : v) {
           sum = sum.add(BigDecimal.valueOf(d).pow(2), ctx);
       }

       return sum.sqrt(ctx).doubleValue();
   }

   /** Compute the difference in ULP between the arguments.
    * @param a first argument
    * @param b second argument
    * @return ULP difference between the arguments
    */
   private static int computeUlpDifference(final double a, final double b) {
       return (int) (Double.doubleToLongBits(a) - Double.doubleToLongBits(b));
   }

   /** Construct a random vector of length {@code len} with double exponent values between
    * {@code minExp} and {@code maxExp}.
    * @param len vector length
    * @param minExp minimum element exponent value
    * @param maxExp maximum element exponent value
    * @param rng random number generator
    * @return random vector array
    */
   private static double[] randomVector(final int len, final int minExp, final int maxExp,
           final UniformRandomProvider rng) {
       final double[] v = new double[len];
       for (int i = 0; i < v.length; ++i) {
           v[i] = randomDouble(minExp, maxExp, rng);
       }
       return v;
   }

   /** Construct a random double with an exponent in the range {@code [minExp, maxExp]}.
    * @param minExp minimum exponent
    * @param maxExp maximum exponent
    * @param rng random number generator
    * @return random double value with an exponent in the specified range
    */
   private static double randomDouble(final int minExp, final int maxExp, final UniformRandomProvider rng) {
       // Create random doubles using random bits in the sign bit and the mantissa.
       final long mask = ((1L << 52) - 1) | 1L << 63;
       final long bits = rng.nextLong() & mask;
       // The exponent must be unsigned so + 1023 to the signed exponent
       final int expRange = Math.abs(maxExp - minExp);
       final long exp = rng.nextInt(expRange) + minExp + 1023;
       return Double.longBitsToDouble(bits | (exp << 52));
   }
}
