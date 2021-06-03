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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PlaneAngleTest {

    @Test
    void testOfTurns() {
        // act
        final PlaneAngle.Turns zero = PlaneAngle.ofTurns(0);
        final PlaneAngle.Turns half = PlaneAngle.ofTurns(0.5);
        final PlaneAngle.Turns minusHalf = PlaneAngle.ofTurns(-0.5);
        final PlaneAngle.Turns full = PlaneAngle.ofTurns(1);

        // act/assert
        Assertions.assertEquals(0d, zero.getValue());
        Assertions.assertEquals(0d, zero.turns());
        Assertions.assertEquals(0d, zero.degrees());
        Assertions.assertEquals(0d, zero.radians());

        Assertions.assertEquals(0.5, half.getValue());
        Assertions.assertEquals(0.5, half.turns());
        Assertions.assertEquals(180d, half.degrees());
        Assertions.assertEquals(Math.PI, half.radians());

        Assertions.assertEquals(-0.5, minusHalf.getValue());
        Assertions.assertEquals(-0.5, minusHalf.turns());
        Assertions.assertEquals(-180d, minusHalf.degrees());
        Assertions.assertEquals(-Math.PI, minusHalf.radians());

        Assertions.assertEquals(1, full.getValue());
        Assertions.assertEquals(1, full.turns());
        Assertions.assertEquals(360d, full.degrees());
        Assertions.assertEquals(2 * Math.PI, full.radians());
    }

    @Test
    void testOfDegrees() {
        // act
        final PlaneAngle.Degrees zero = PlaneAngle.ofDegrees(0);
        final PlaneAngle.Degrees half = PlaneAngle.ofDegrees(180);
        final PlaneAngle.Degrees minusHalf = PlaneAngle.ofDegrees(-180);
        final PlaneAngle.Degrees full = PlaneAngle.ofDegrees(360);

        // act/assert
        Assertions.assertEquals(0d, zero.getValue());
        Assertions.assertEquals(0d, zero.turns());
        Assertions.assertEquals(0d, zero.degrees());
        Assertions.assertEquals(0d, zero.radians());

        Assertions.assertEquals(180, half.getValue());
        Assertions.assertEquals(0.5, half.turns());
        Assertions.assertEquals(180d, half.degrees());
        Assertions.assertEquals(Math.PI, half.radians());

        Assertions.assertEquals(-180, minusHalf.getValue());
        Assertions.assertEquals(-0.5, minusHalf.turns());
        Assertions.assertEquals(-180d, minusHalf.degrees());
        Assertions.assertEquals(-Math.PI, minusHalf.radians());

        Assertions.assertEquals(360, full.getValue());
        Assertions.assertEquals(1, full.turns());
        Assertions.assertEquals(360d, full.degrees());
        Assertions.assertEquals(2 * Math.PI, full.radians());
    }

    @Test
    void testOfRadians() {
        // act
        final PlaneAngle.Radians zero = PlaneAngle.ofRadians(0);
        final PlaneAngle.Radians half = PlaneAngle.ofRadians(Math.PI);
        final PlaneAngle.Radians minusHalf = PlaneAngle.ofRadians(-Math.PI);
        final PlaneAngle.Radians full = PlaneAngle.ofRadians(2 * Math.PI);

        // act/assert
        Assertions.assertEquals(0d, zero.getValue());
        Assertions.assertEquals(0d, zero.turns());
        Assertions.assertEquals(0d, zero.degrees());
        Assertions.assertEquals(0d, zero.radians());

        Assertions.assertEquals(Math.PI, half.getValue());
        Assertions.assertEquals(0.5, half.turns());
        Assertions.assertEquals(180d, half.degrees());
        Assertions.assertEquals(Math.PI, half.radians());

        Assertions.assertEquals(-Math.PI, minusHalf.getValue());
        Assertions.assertEquals(-0.5, minusHalf.turns());
        Assertions.assertEquals(-180d, minusHalf.degrees());
        Assertions.assertEquals(-Math.PI, minusHalf.radians());

        Assertions.assertEquals(2 * Math.PI, full.getValue());
        Assertions.assertEquals(1, full.turns());
        Assertions.assertEquals(360d, full.degrees());
        Assertions.assertEquals(2 * Math.PI, full.radians());
    }

    @Test
    public void t() {
        double lower = Math.PI / 3;
        double center = lower + Math.PI;
        System.out.println(lower);
        System.out.println(center - Math.PI);


    }
}
