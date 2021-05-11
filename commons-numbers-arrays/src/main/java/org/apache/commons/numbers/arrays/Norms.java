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

public final class Norms {

    private static final double SMALL_THRESH = 0x1.0p-500;

    private static final double LARGE_THRESH = 0x1.0p+500;

    private static final double SCALE_DOWN = 0x1.0p-600;

    private static final double SCALE_UP = 0x1.0p+600;

    /** Utility class; no instantiation. */
    private Norms() {}

    public static double manhattan(final double x) {
        return Math.abs(x);
    }

    public static double manhattan(final double x, final double y) {
        return Math.abs(x) + Math.abs(y);
    }

    public static double manhattan(final double x, final double y, final double z) {
        return Math.abs(x) + Math.abs(y) + Math.abs(z);
    }

    public static double manhattan(final double[] v) {
        double s = 0;
        for (int i = 0; i < v.length; ++i) {
            s += Math.abs(v[i]);
        }
        return s;
    }

    public static double euclidean(final double x) {
        return Math.abs(x);
    }

    public static double euclidean(final double x, final double y) {
        // TODO
        return euclidean(new double[] {x, y});
    }

    public static double euclidean(final double x, final double y, final double z) {
        // TODO
        return euclidean(new double[] {x, y, z});
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

        // The highest sum is the significant component. Add the next significant.
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

    private static double square(final double x) {
        return x * x;
    }
}
