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
                if (x > 0x1.0p500) {
                    // Scale down big numbers
                    final double y = square(x * 0x1.0p-600) - c1;
                    final double t = s1 + y;
                    c1 = (t - s1) - y;
                    s1 = t;

                } else if (x < 0x1.0p-500) {
                    // Scale up small numbers
                    final double y = square(x * 0x1.0p600) - c3;
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
}
