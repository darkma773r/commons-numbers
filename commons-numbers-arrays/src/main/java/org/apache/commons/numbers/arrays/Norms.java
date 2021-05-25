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

/** Class containing methods to compute various norm values.
 * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)">Norm</a>
 */
public final class Norms {

    /** Threshold for scaling small numbers. */
    private static final double SMALL_THRESH = 0x1.0p-500;

    /** Threshold for scaling large numbers. */
    private static final double LARGE_THRESH = 0x1.0p+500;

    /** Value used to scale down large numbers. */
    private static final double SCALE_DOWN = 0x1.0p-600;

    /** Value used to scale up small numbers. */
    private static final double SCALE_UP = 0x1.0p+600;

    /** Utility class; no instantiation. */
    private Norms() {}

    /** Compute the Manhattan norm (also known as the Taxicab norm or L1 norm) of the arguments.
     * The result is equal to \(|x| + |y|\), i.e., the sum of the absolute values of the arguments.
     * @param x first input value
     * @param y second input value
     * @return Manhattan norm or NaN if any input is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double x, final double y) {
        return Math.abs(x) + Math.abs(y);
    }

    /** Compute the Manhattan norm (also known as the Taxicab norm or L1 norm) of the arguments.
     * The result is equal to \(|x| + |y| + |z|\), i.e., the sum of the absolute values of the arguments.
     * @param x first input value
     * @param y second input value
     * @param z third input value
     * @return Manhattan norm or NaN if any input is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double x, final double y, final double z) {
        return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    /** Compute the Manhattan norm (also known as the Taxicab norm or L1 norm) of the given values.
     * The result is equal to \(|v_0| + ... + |v_i|\), i.e., the sum of the absolute values of the input elements.
     * @param v input values
     * @return Manhattan norm, NaN if any element is NaN, or 0 if the input array is empty
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double[] v) {
        double s = 0d;
        for (int i = 0; i < v.length; ++i) {
            s += Math.abs(v[i]);
        }
        return s;
    }

    /** Compute the Euclidean norm (also known as the L2 norm) of the arguments. The result is equal to
     * \(\sqrt{x^2 + y^2}\).
     * @param x first input
     * @param y second input
     * @return Euclidean norm of the arguments or NaN if any value is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm">Euclidean norm</a>
     */
    public static double euclidean(final double x, final double y) {
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;

        final double xAbs = Math.abs(x);
        if (xAbs > LARGE_THRESH) {
            s1 += square(xAbs * SCALE_DOWN);
        } else if (xAbs < SMALL_THRESH) {
            s3 += square(xAbs * SCALE_UP);
        } else {
            s2 += square(xAbs);
        }

        final double yAbs = Math.abs(y);
        if (yAbs > LARGE_THRESH) {
            s1 += square(yAbs * SCALE_DOWN);
        } else if (yAbs < SMALL_THRESH) {
            s3 += square(yAbs * SCALE_UP);
        } else {
            s2 += square(yAbs);
        }

        return euclideanNormFromScaled(s1, s2, s3);
    }

    /** Compute the Euclidean norm (also known as the L2 norm) of the arguments. The result is equal to
     * \(\sqrt{x^2 + y^2 + z^2}\).
     * @param x first input
     * @param y second input
     * @param z third input
     * @return Euclidean norm of the arguments or NaN if any value is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm">Euclidean norm</a>
     */
    public static double euclidean(final double x, final double y, final double z) {
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;

        final double xAbs = Math.abs(x);
        if (xAbs > LARGE_THRESH) {
            s1 += square(xAbs * SCALE_DOWN);
        } else if (xAbs < SMALL_THRESH) {
            s3 += square(xAbs * SCALE_UP);
        } else {
            s2 += square(xAbs);
        }

        final double yAbs = Math.abs(y);
        if (yAbs > LARGE_THRESH) {
            s1 += square(yAbs * SCALE_DOWN);
        } else if (yAbs < SMALL_THRESH) {
            s3 += square(yAbs * SCALE_UP);
        } else {
            s2 += square(yAbs);
        }

        final double zAbs = Math.abs(z);
        if (zAbs > LARGE_THRESH) {
            s1 += square(zAbs * SCALE_DOWN);
        } else if (zAbs < SMALL_THRESH) {
            s3 += square(zAbs * SCALE_UP);
        } else {
            s2 += square(zAbs);
        }

        return euclideanNormFromScaled(s1, s2, s3);
    }

    /** Compute the Euclidean norm (also known as the L2 norm) of the given values. The result is equal to
     * \(\sqrt{v_0^2 + ... + v_i^2}\).
     * @param v input values
     * @return Euclidean norm of the input values, NaN if any element is NaN, or 0 if the input array
     *      is empty
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm">Euclidean norm</a>
     */
    public static double euclidean(final double[] v) {
        // sum of big, normal and small numbers
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;

        // sum correction values
        double c1 = 0;
        double c2 = 0;
        double c3 = 0;

        for (int i = 0; i < v.length; ++i) {
            final double x = Math.abs(v[i]);
            if (x > LARGE_THRESH) {
                // scale down big numbers
             // scale down
                final double sx = x * SCALE_DOWN;

                // compute the product and product correction
                final double p = sx * sx;
                final double cp = productLowUnscaled(sx, p);

                // compute the running sum and sum correction
                final double s = s1 + p;
                final double cs = DoublePrecision.twoSumLow(s1, p, s);

                // update running totals
                c1 += cp + cs;
                s1 = s;
            } else if (x < SMALL_THRESH) {
                // scale up small numbers
                s3 += square(x * SCALE_UP);
            } else {
                // unscaled
                s2 += square(x);
            }
        }

        return euclideanNormFromScaled(s1, s2, s3);
    }

    /** Compute the Euclidean norm from high, mid, and low scaled sums.
     * @param s1 high scaled sum
     * @param s2 mid sum
     * @param s3 low scaled sum
     * @return Euclidean norm
     */
    private static double euclideanNormFromScaled(final double s1, final double s2, final double s3) {
        // The highest sum is the significant component. Add the next significant.
        // Note that the "x * SCALE_DOWN * SCALE_DOWN" expressions must be executed
        // in the order given. If the two scale factors are multiplied together first,
        // they will underflow to zero.
        if (s1 != 0) {
            return Math.sqrt(s1 + (s2 * SCALE_DOWN * SCALE_DOWN)) * SCALE_UP;
        } else if (s2 != 0) {
            return Math.sqrt(s2 + (s3 * SCALE_DOWN * SCALE_DOWN));
        }
        return Math.sqrt(s3) * SCALE_DOWN;
    }

    /**
     * Compute the low part of the double length number {@code (z,zz)} for the exact
     * square of {@code x} using Dekker's mult12 algorithm. The standard
     * precision product {@code x*x} must be provided. The number {@code x}
     * is split into high and low parts using Dekker's algorithm.
     *
     * <p>Warning: This method does not perform scaling in Dekker's split and large
     * finite numbers can create NaN results.
     *
     * @param x The factor.
     * @param xx Square of the factor (x * x).
     * @return the low part of the product double length number
     */
   private static double productLowUnscaled(double x, double xx) {
       // Split the numbers using Dekker's algorithm without scaling
       final double hx = DoublePrecision.highPartUnscaled(x);
       final double lx = x - hx;

       return DoublePrecision.productLow(hx, lx, hx, lx, xx);
   }

    /** Compute the maximum norm (also known as the infinity norm or L<sub>inf</sub> norm) of the arguments.
     * The result is equal to \(\max{(|x|, |y|)}\), i.e., the maximum of the absolute values of the arguments.
     * @param x first input
     * @param y second input
     * @return the maximum norm of the arguments or NaN if any value is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Maximum_norm_(special_case_of:_infinity_norm,_uniform_norm,_or_supremum_norm)">Maximum norm</a>
     */
    public static double maximum(final double x, final double y) {
        return Math.max(Math.abs(x), Math.abs(y));
    }

    /** Compute the maximum norm (also known as the infinity norm or L<sub>inf</sub> norm) of the arguments.
     * The result is equal to \(\max{(|x|, |y|, |z|)}\), i.e., the maximum of the absolute values of the arguments.
     * @param x first input
     * @param y second input
     * @param z third input
     * @return the maximum norm of the arguments or NaN if any value is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Maximum_norm_(special_case_of:_infinity_norm,_uniform_norm,_or_supremum_norm)">Maximum norm</a>
     */
    public static double maximum(final double x, final double y, final double z) {
        return Math.max(
                Math.abs(x),
                Math.max(Math.abs(y), Math.abs(z)));
    }

    /** Compute the maximum norm (also known as the infinity norm or L<sub>inf</sub> norm) of the given values.
     * The result is equal to \(\max{(|v_0|, ... |v_i|)}\), i.e., the maximum of the absolute values of the
     * input elements.
     * @param v input values
     * @return the maximum norm of the inputs, NaN if any value is NaN, or 0 if the input array is empty
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Maximum_norm_(special_case_of:_infinity_norm,_uniform_norm,_or_supremum_norm)">Maximum norm</a>
     */
    public static double maximum(final double[] v) {
        double max = 0d;
        for (int i = 0; i < v.length; ++i) {
            max = Math.max(max, Math.abs(v[i]));
        }
        return max;
    }

    /** Return the square of {@code x}.
     * @param x number to square
     * @return x * x
     */
    private static double square(final double x) {
        return x * x;
    }
}
