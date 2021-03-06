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
package org.apache.commons.numbers.angle;

/**
 * Utility class where all {@code double} values are assumed to be in
 * radians.
 *
 * @see PlaneAngle
 */
public final class PlaneAngleRadians {
    /** Value of \( \pi \): {@value}. */
    public static final double PI = Math.PI;
    /** Value of \( 2\pi \): {@value}. */
    public static final double TWO_PI = 2 * PI;
    /** Value of \( \pi/2 \): {@value}. */
    public static final double PI_OVER_TWO = 0.5 * PI;
    /** Value of \( 3\pi/2 \): {@value}. */
    public static final double THREE_PI_OVER_TWO = 3 * PI_OVER_TWO;

    /** Utility class. */
    private PlaneAngleRadians() {}

    /**
     * Normalize an angle in an interval of size 2&pi; around a
     * center value.
     *
     * @param angle Value to be normalized.
     * @param center Center of the desired interval for the result.
     * @return {@code a - 2 * k} with integer {@code k} such that
     * {@code center - pi <= a - 2 * k * pi < center + pi}.
     */
    public static double normalize(double angle,
                                   double center) {
        final PlaneAngle a = PlaneAngle.ofRadians(angle);
        final PlaneAngle c = PlaneAngle.ofRadians(center);
        return a.normalize(c).toRadians();
    }

    /**
     * Normalize an angle to be in the range [-&pi;, &pi;).
     *
     * @param angle Value to be normalized.
     * @return {@code a - 2 * k} with integer {@code k} such that
     * {@code -pi <= a - 2 * k * pi < pi}.
     */
    public static double normalizeBetweenMinusPiAndPi(double angle) {
        return PlaneAngle.ofRadians(angle).normalize(PlaneAngle.ZERO).toRadians();
    }

    /**
     * Normalize an angle to be in the range [0, 2&pi;).
     *
     * @param angle Value to be normalized.
     * @return {@code a - 2 * k} with integer {@code k} such that
     * {@code 0 <= a - 2 * k * pi < 2 * pi}.
     */
    public static double normalizeBetweenZeroAndTwoPi(double angle) {
        return PlaneAngle.ofRadians(angle).normalize(PlaneAngle.PI).toRadians();
    }
}
