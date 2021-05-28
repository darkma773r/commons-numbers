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

/** Class providing accurate floating-point summations.
 */
public final class Summation {

    /** Utility class; no instantiation. */
    private Summation() {}

    /** Compute the sum of the input values.
     * @param a first value
     * @param b second value
     * @param c third value
     * @return sum of the input values
     */
    public static double value(final double a, final double b, final double c) {
        double sum = a;
        double corr = 0d;

        final double sb = sum + b;
        corr += ExtendedPrecision.twoSumLow(sum, b, sb);
        sum = sb;

        final double sc = sum + c;
        corr += ExtendedPrecision.twoSumLow(sum, c, sc);
        sum = sc;

        final double result = sum + corr;
        if (!Double.isFinite(result)) {
            // non-finite result; fall back to standard summation
            return a + b + c;
        }

        return result;
    }

    /** Compute the sum of the input values.
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     * @return sum of the input values
     */
    public static double value(final double a, final double b, final double c, final double d) {
        double sum = a;
        double corr = 0d;

        final double sb = sum + b;
        corr += ExtendedPrecision.twoSumLow(sum, b, sb);
        sum = sb;

        final double sc = sum + c;
        corr += ExtendedPrecision.twoSumLow(sum, c, sc);
        sum = sc;

        final double sd = sum + d;
        corr += ExtendedPrecision.twoSumLow(sum, d, sd);
        sum = sd;

        final double result = sum + corr;
        if (!Double.isFinite(result)) {
            // non-finite result; fall back to standard summation
            return a + b + c + d ;
        }

        return result;
    }

    /** Compute the sum of the input values.
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     * @param e fifth value
     * @return sum of the input values
     */
    public static double value(final double a, final double b, final double c, final double d,
            final double e) {
        double sum = a;
        double corr = 0d;

        final double sb = sum + b;
        corr += ExtendedPrecision.twoSumLow(sum, b, sb);
        sum = sb;

        final double sc = sum + c;
        corr += ExtendedPrecision.twoSumLow(sum, c, sc);
        sum = sc;

        final double sd = sum + d;
        corr += ExtendedPrecision.twoSumLow(sum, d, sd);
        sum = sd;

        final double se = sum + e;
        corr += ExtendedPrecision.twoSumLow(sum, e, se);
        sum = se;

        final double result = sum + corr;
        if (!Double.isFinite(result)) {
            // non-finite result; fall back to standard summation
            return a + b + c + d + e;
        }

        return result;
    }

    /** Compute the sum of the input values.
     * @param a first value
     * @param b second value
     * @param c third value
     * @param d fourth value
     * @param e fifth value
     * @param f sixth value
     * @return sum of the input values
     */
    public static double value(final double a, final double b, final double c, final double d,
            final double e, final double f) {
        double sum = a;
        double corr = 0d;

        final double sb = sum + b;
        corr += ExtendedPrecision.twoSumLow(sum, b, sb);
        sum = sb;

        final double sc = sum + c;
        corr += ExtendedPrecision.twoSumLow(sum, c, sc);
        sum = sc;

        final double sd = sum + d;
        corr += ExtendedPrecision.twoSumLow(sum, d, sd);
        sum = sd;

        final double se = sum + e;
        corr += ExtendedPrecision.twoSumLow(sum, e, se);
        sum = se;

        final double sf = sum + f;
        corr += ExtendedPrecision.twoSumLow(sum, f, sf);
        sum = sf;

        final double result = sum + corr;
        if (!Double.isFinite(result)) {
            // non-finite result; fall back to standard summation
            return a + b + c + d + e + f;
        }

        return result;
    }

    /** Compute the sum of the input values.
     * @param a array containing values to sum
     * @return sum of the input values
     */
    public static double value(final double[] a) {
        double sum = 0d;
        double corr = 0d;

        for (final double x : a) {
            final double s = sum + x;
            corr += ExtendedPrecision.twoSumLow(sum, x, s);
            sum = s;
        }

        final double result = sum + corr;
        if (!Double.isFinite(result)) {
            // non-finite result; fall back to standard summation
            return computeStandard(a);
        }

        return result;
    }

    /** Compute the standard sum of the given values using direct double operations.
     * This is used in edge cases to produce results consistent with IEEE 754 rules.
     * @param a input values
     * @return standard sum
     */
    private static double computeStandard(final double[] a) {
        double sum = 0;
        for (final double x : a) {
            sum += x;
        }
        return sum;
    }
}
