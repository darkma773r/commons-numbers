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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SummationTest {

    private static final int MAX_ULP_ERR = 1;

    @Test
    void testSimple_array() {
        // act/assert
        assertArraySum(0);
        assertArraySum(0, 0, 0, 0);

        assertArraySum(1, 1, 1);
        assertArraySum(-1, -1, -1);

        assertArraySum(Math.PI, Math.E);
    }

    @Test
    public void testSpecialCases_array() {
        // act/assert
        Assertions.assertEquals(0d, Summation.value(new double[0]));

        Assertions.assertEquals(Double.MAX_VALUE, Summation.value(new double[] {Double.MAX_VALUE}));
        Assertions.assertEquals(Double.MIN_VALUE, Summation.value(new double[] {Double.MIN_VALUE}));

        Assertions.assertEquals(Double.NaN, Summation.value(new double[] {0d, Double.NaN}));
        Assertions.assertEquals(Double.NaN, Summation.value(new double[] {Double.POSITIVE_INFINITY, Double.NaN}));
        Assertions.assertEquals(Double.NaN,
                Summation.value(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));

        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Summation.value(new double[] {Double.MAX_VALUE, Double.MAX_VALUE}));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Summation.value(new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY}));
    }

    private static void assertArraySum(final double... values) {
        final double exact = computeExact(values);
        final double actual = Summation.value(values);

        final int ulpError = DoubleTestUtils.computeUlpDifference(exact, actual);

        Assertions.assertTrue(ulpError <= MAX_ULP_ERR, () ->
        "Computed norm ulp error exceeds bounds; values= " + Arrays.toString(values) +
        ", exact= " + exact + ", actual= " + actual + ", ulpError= " + ulpError);
    }

    private static double computeExact(final double... values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (double value : values) {
            sum = sum.add(BigDecimal.valueOf(value), MathContext.UNLIMITED);
        }

        return sum.doubleValue();
    }
}
