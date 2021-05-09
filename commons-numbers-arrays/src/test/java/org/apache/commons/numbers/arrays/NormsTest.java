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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NormsTest {

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
    public void testEuclidean_array_simple() {
        // act/assert
        Assertions.assertEquals(0d, Norms.euclidean(new double[0]));
        Assertions.assertEquals(5d, Norms.euclidean(new double[] {-3d, 4d}));

        Assertions.assertEquals(Math.sqrt(2), Norms.euclidean(new double[] {1d, -1d}));
        Assertions.assertEquals(Math.sqrt(3), Norms.euclidean(new double[] {1d, -1d, 1d}));
        Assertions.assertEquals(2, Norms.euclidean(new double[] {1d, -1d, 1d, -1d}));

        Assertions.assertEquals(Double.NaN, Norms.euclidean(new double[] {-2d, Double.NaN, 1d}));
        Assertions.assertEquals(Double.POSITIVE_INFINITY,
                Norms.euclidean(new double[] {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}));
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
}
