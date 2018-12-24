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
package org.apache.commons.numbers.core.precision;

import java.util.Comparator;

/** Class encapsulating the concept of comparison operations for doubles.
 */
public abstract class DoublePrecisionContext implements Comparator<Double> {

    /** Return true if the given values should be considered equal to
     * each other.
     * @param a first value
     * @param b second value
     * @return true if the given values should be considered equal
     */
    public abstract boolean equals(final double a, final double b);

    /** Compare two double values. The returned value is
     * <ul>
     *  <li>
     *   0 if  {@link #equals(double,double)} returns true,
     *  </li>
     *  <li>
     *   negative if !{@link #equals(double,double)} and {@code x < y},
     *  </li>
     *  <li>
     *   positive if !{@link #equals(double,double)} and {@code x > y} or
     *   either argument is {@code NaN}.
     *  </li>
     * </ul>
     *
     * @param a first value
     * @param b second value
     * @return 0 if the value are considered equal, -1 if the first is smaller than
     * the second, 1 is the first is larger than the second.
     */
    public int compare(final double a, final double b) {
        if (equals(a, b)) {
            return 0;
        }
        else if (a < b) {
            return -1;
        }
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public int compare(final Double a, final Double b) {
        return compare(a.doubleValue(), b.doubleValue());
    }
}
