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
package org.apache.commons.numbers.examples.jmh.arrays;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.ToDoubleFunction;

/** Class containing various Euclidean norm computation methods for comparison.
 */
public final class EuclideanNormAlgorithms {

    /** No instantiation. */
    private EuclideanNormAlgorithms() {}

    /** Exact computation method using {@link BigDecimal} and {@link MathContext#DECIMAL128}.
     */
    static final class Exact implements ToDoubleFunction<double[]> {

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            // compute the sum of squares
            final MathContext ctx = MathContext.DECIMAL128;

            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal n;
            for (int i = 0; i < v.length; ++i) {
                n = BigDecimal.valueOf(Double.isFinite(v[i]) ? v[i] : 0d);
                sum = sum.add(n.multiply(n, ctx), ctx);
            }

            return sum.sqrt(ctx).doubleValue();
        }
    }

    /** Direct computation method that simply computes the sums of squares and takes
     * the square root with no special handling of values.
     */
    static final class Direct implements ToDoubleFunction<double[]> {

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            double n = 0;
            for (int i = 0; i < v.length; i++) {
                n += v[i] * v[i];
            }
            return Math.sqrt(n);
        }
    }

    /** Translation of the <a href="http://www.netlib.org/minpack">minpack</a>
     * "enorm" subroutine. This method handles overflow and underflow.
     */
    static final class Enorm implements ToDoubleFunction<double[]> {

        /** Constant. */
        private static final double R_DWARF = 3.834e-20;
        /** Constant. */
        private static final double R_GIANT = 1.304e+19;

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double x1max = 0;
            double x3max = 0;
            final double floatn = v.length;
            final double agiant = R_GIANT / floatn;
            for (int i = 0; i < v.length; i++) {
                final double xabs = Math.abs(v[i]);
                if (xabs < R_DWARF || xabs > agiant) {
                    if (xabs > R_DWARF) {
                        if (xabs > x1max) {
                            final double r = x1max / xabs;
                            s1 = 1 + s1 * r * r;
                            x1max = xabs;
                        } else {
                            final double r = xabs / x1max;
                            s1 += r * r;
                        }
                    } else {
                        if (xabs > x3max) {
                            final double r = x3max / xabs;
                            s3 = 1 + s3 * r * r;
                            x3max = xabs;
                        } else {
                            if (xabs != 0) {
                                final double r = xabs / x3max;
                                s3 += r * r;
                            }
                        }
                    }
                } else {
                    s2 += xabs * xabs;
                }
            }
            double norm;
            if (s1 != 0) {
                norm = x1max * Math.sqrt(s1 + (s2 / x1max) / x1max);
            } else {
                if (s2 == 0) {
                    norm = x3max * Math.sqrt(s3);
                } else {
                    if (s2 >= x3max) {
                        norm = Math.sqrt(s2 * (1 + (x3max / s2) * (x3max * s3)));
                    } else {
                        norm = Math.sqrt(x3max * ((s2 / x3max) + (x3max * s3)));
                    }
                }
            }
            return norm;
        }
    }

    /** Modified version of {@link Enorm} created by Alex Herbert.
     */
    static final class EnormMod implements ToDoubleFunction<double[]> {

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            // Sum of big, normal and small numbers
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (x > 0x1.0p500) {
                    // Scale down big numbers
                    s1 += square(x * 0x1.0p-600);
                } else if (x < 0x1.0p-500) {
                    // Scale up small numbers
                    s3 += square(x * 0x1.0p600);
                } else {
                    // Unscaled
                    s2 += square(x);
                }
            }
            // The highest sum is the significant component. Add the next significant.
            if (s1 != 0) {
                return Math.sqrt(s1 + s2 * 0x1.0p-600 * 0x1.0p-600) * 0x1.0p600;
            } else if (s2 != 0) {
                return Math.sqrt(s2 + s3 * 0x1.0p-600 * 0x1.0p-600);
            }
            return Math.sqrt(s3) * 0x1.0p-600;
        }

        /** Compute the square of {@code x}.
         * @param x input value
         * @return square of {@code x}
         */
        private static double square(final double x) {
            return x * x;
        }
    }

    /** Version of {@link EnormMod} using Kahan summation.
     */
    static final class EnormModKahan implements ToDoubleFunction<double[]> {

        /** Threshold for scaling small numbers. */
        private static final double SMALL_THRESH = 0x1.0p-500;

        /** Threshold for scaling large numbers. */
        private static final double LARGE_THRESH = 0x1.0p+500;

        /** Value used to scale down large numbers. */
        private static final double SCALE_DOWN = 0x1.0p-600;

        /** Value used to scale up small numbers. */
        private static final double SCALE_UP = 0x1.0p+600;

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            // Sum of big, normal and small numbers
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (x > LARGE_THRESH) {
                    // Scale down big numbers
                    final double y = square(x * SCALE_DOWN) - c1;
                    final double t = s1 + y;
                    c1 = (t - s1) - y;
                    s1 = t;

                } else if (x < SMALL_THRESH) {
                    // Scale up small numbers
                    final double y = square(x * SCALE_UP) - c3;
                    final double t = s3 + y;
                    c3 = (t - s3) - y;
                    s3 = t;
                } else {
                    // Unscaled
                    final double y = square(x) - c2;
                    final double t = s2 + y;
                    c2 = (t - s2) - y;
                    s2 = t;
                }
            }
            // The highest sum is the significant component. Add the next significant.
            // Add the scaled compensation then the scaled sum.
            if (s1 != 0) {
                double y = c2 * SCALE_DOWN * SCALE_DOWN - c1;
                final double t = s1 + y;
                c1 = (t - s1) - y;
                y = s2 * SCALE_DOWN * SCALE_DOWN - c1;
                return Math.sqrt(t + y) * SCALE_UP;
            } else if (s2 != 0) {
                double y = c3 * SCALE_DOWN * SCALE_DOWN - c2;
                final double t = s2 + y;
                c2 = (t - s2) - y;
                y = s3 * SCALE_DOWN * SCALE_DOWN - c2;
                return Math.sqrt(t + y);
            }
            return Math.sqrt(s3) * SCALE_DOWN;
        }

        /** Compute the square of {@code x}.
         * @param x input value
         * @return square of {@code x}
         */
        private static double square(final double x) {
            return x * x;
        }
    }

    /** Version of {@link EnormMod} using extended precision summation.
     */
    static final class EnormModExt implements ToDoubleFunction<double[]> {

        /** Threshold for scaling small numbers. */
        private static final double SMALL_THRESH = 0x1.0p-500;

        /** Threshold for scaling large numbers. */
        private static final double LARGE_THRESH = 0x1.0p+500;

        /** Value used to scale down large numbers. */
        private static final double SCALE_DOWN = 0x1.0p-600;

        /** Value used to scale up small numbers. */
        private static final double SCALE_UP = 0x1.0p+600;

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
            // Sum of big, normal and small numbers with 2-fold extended precision summation
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (x > LARGE_THRESH) {
                    // Scale down big numbers
                    final double y = square(x * SCALE_DOWN) + c1;
                    final double t = s1 + y;
                    c1 = DoublePrecision.twoSumLow(s1, y, t);
                    s1 = t;
                } else if (x < SMALL_THRESH) {
                    // Scale up small numbers
                    final double y = square(x * SCALE_UP) + c3;
                    final double t = s3 + y;
                    c3 = DoublePrecision.twoSumLow(s3, y, t);
                    s3 = t;
                } else {
                    // Unscaled
                    final double y = square(x) + c2;
                    final double t = s2 + y;
                    c2 = DoublePrecision.twoSumLow(s2, y, t);
                    s2 = t;
                }
            }
            // The highest sum is the significant component. Add the next significant.
            // Adapted from LinearCombination dot2s summation.
            if (s1 != 0) {
                s2 = s2 * SCALE_DOWN * SCALE_DOWN;
                c2 = c2 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s1 + s2;
                // Add the round-off from the main sum to the other round-off components
                c1 += DoublePrecision.twoSumLow(s1, s2, sum) + c2;
                return Math.sqrt(sum + c1) * SCALE_UP;
            } else if (s2 != 0) {
                s3 = s3 * SCALE_DOWN * SCALE_DOWN;
                c3 = c3 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s2 + s3;
                // Add the round-off from the main sum to the other round-off components
                c2 += DoublePrecision.twoSumLow(s2, s3, sum) + c3;
                return Math.sqrt(sum + c2);
            }
            return Math.sqrt(s3) * SCALE_DOWN;
        }

        /** Compute the square of {@code x}.
         * @param x input value
         * @return square of {@code x}
         */
        private static double square(final double x) {
            return x * x;
        }
    }

    /** Euclidean norm computation algorithm that uses {@link LinearCombinations} to perform
     * an extended precision summation.
     */
    static final class ExtendedPrecisionLinearCombination implements ToDoubleFunction<double[]> {

        /** {@inheritDoc} */
        @Override
        public double applyAsDouble(final double[] v) {
         // Find the magnitude limits ignoring zero
            double max = 0;
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (x > max) {
                    max = x;
                } else if (x < min && x != 0) {
                    min = x;
                }
            }
            // Edge case
            if (max == 0) {
                return 0;
            }
            // Use scaling if required
            double[] x = v;
            double rescale = 1;
            if (max > 0x1.0p500) {
                // Too big so scale down
                x = x.clone();
                for (int i = 0; i < x.length; i++) {
                    x[i] *= 0x1.0p-600;
                }
                rescale = 0x1.0p600;
            } else if (min < 0x1.0p-500) {
                // Too small so scale up
                x = x.clone();
                for (int i = 0; i < x.length; i++) {
                    x[i] *= 0x1.0p600;
                }
                rescale = 0x1.0p-600;
            }
            return Math.sqrt(org.apache.commons.numbers.arrays.LinearCombination.value(x, x)) * rescale;
        }
    }
}
