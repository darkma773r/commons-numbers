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

    /** Compute the Manhattan norm of the argument, also known as the Taxicab norm or L1 norm.
     * The result is simply the absolute value of the argument.
     * @param x input value
     * @return Manhattan norm of {@code x} or NaN if {@code x} is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double x) {
        return Math.abs(x);
    }

    /** Compute the Manhattan norm of the arguments, also known as the Taxicab norm or L1 norm.
     * The result is simply the sum of the absolute values of the arguments.
     * @param x first input value
     * @param y second input value
     * @return Manhattan norm or NaN if any input is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double x, final double y) {
        return Math.abs(x) + Math.abs(y);
    }

    /** Compute the Manhattan norm of the arguments, also known as the Taxicab norm or L1 norm.
     * The result is simply the sum of the absolute values of the arguments.
     * @param x first input value
     * @param y second input value
     * @param z third input value
     * @return Manhattan norm or NaN if any input is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double x, final double y, final double z) {
        return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    /** Compute the Manhattan norm of given values, also known as the Taxicab norm or L1 norm.
     * The result is simply the sum of the absolute values of the inputs.
     * @param v input values
     * @return Manhattan norm or NaN if any element is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Taxicab_norm_or_Manhattan_norm">Manhattan norm</a>
     */
    public static double manhattan(final double[] v) {
        double s = 0d;
        for (int i = 0; i < v.length; ++i) {
            s += Math.abs(v[i]);
        }
        return s;
    }

    /** Compute the Euclidean norm of the argument, also known as the L2 norm. With a single argument,
     * this is simply the absolute value of the input.
     * @param x input value
     * @return Euclidean norm of {@code x} or NaN if {@code x} is NaN
     * @see <a href="https://en.wikipedia.org/wiki/Norm_(mathematics)#Euclidean_norm">Euclidean norm</a>
     */
    public static double euclidean(final double x) {
        return Math.abs(x);
    }

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

    public static double euclidean(final double[] v) {
        // Sum of big, normal and small numbers
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;

        for (int i = 0; i < v.length; ++i) {
            final double x = Math.abs(v[i]);
            if (x > LARGE_THRESH) {
                // Scale down big numbers
                s1 += square(x * SCALE_DOWN);
            } else if (x < SMALL_THRESH) {
                // Scale up small numbers
                s3 += square(x * SCALE_UP);
            } else {
                // Unscaled
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

    public static double maximum(final double x) {
        return Math.abs(x);
    }

    public static double maximum(final double x, final double y) {
        return Math.max(Math.abs(x), Math.abs(y));
    }

    public static double maximum(final double x, final double y, final double z) {
        return Math.max(
                Math.abs(x),
                Math.max(Math.abs(y), Math.abs(z)));
    }

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
