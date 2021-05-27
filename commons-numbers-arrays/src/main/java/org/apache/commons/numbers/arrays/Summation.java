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

/** Class providing high-precision summations.
 */
public final class Summation {

    /** Utility class; no instantiation. */
    private Summation() {}

    /** Compute the sum of the given values.
     * @param a array containing values to sum
     * @return sum of input values
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
