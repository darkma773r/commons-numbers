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

import java.io.Serializable;

import org.apache.commons.numbers.core.Precision;

/** {@link DoublePrecisonContext} subclass that uses a combination of an absolute
 * epsilon value and a maximum units in place (ulp) value to determine equality
 * between doubles.
 *
 * <p>The equality algorithm proceeds as follows:
 * <ol>
 *      <li>If the absolute difference between the two inputs is less than or equal to the epsilon
 *      value (i.e. {@code Math.abs(a - b) <= eps}), then the two numbers are considered equal.
 *      Otherwise, the algorithm continues.</li>
 *      <li>The numbers are compared using the "units in last place" (ulp) comparison
 *      from {@link Precision#equals(float, float, int)} with the last argument set to
 *      {@code maxUlps}. If this returns true, then the numbers are considered equal.
 *      Otherwise, they are not considered equal.</li>
 *</ol>
 * This two-phase approach is used in order to allow reasonable answers to be given
 * for a wide range of numbers. Absolute epsilon values are best for comparisons against
 * zero while units in last place comparisons are required when working with very large
 * numbers (since the difference between adjacent large floating point values may be larger
 * than the epsilon value itself). This approach is based on the ideas in the article
 * found <a href="https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/">here</a>.
 * </p>
 */
public class EpsilonUlpDoublePrecisionContext extends DoublePrecisionContext implements Serializable {

    /** Serializable identifer */
    private static final long serialVersionUID = 20181223L;

    /** Absolute epsilon value. */
    private final double eps;

    /** Maximum number of units in last place that may be different between two doubles
     * while still considering them equal.
     */
    private final int maxUlps;

    /** Simple constructor.
     * @param eps Absolute epsilon value. Numbers with a difference less than or equal
     *      to this value are considered equal.
     * @param maxUlps Maximum units in last place that may differ between numbers while
     *      still considering them equal.
     */
    public EpsilonUlpDoublePrecisionContext(final double eps, final int maxUlps) {
        this.eps = eps;
        this.maxUlps = maxUlps;
    }

    /** Get the absolute epsilon value for the instance. Numbers with a difference less
     * than or equal to this value are considered equal.
     * @return the absolute epsilon value for the instance
     */
    public double getEps() {
        return eps;
    }

    /** Get the maximum ulps value for the instance. This is the maximum units in last place
     * that may differ between two doubles while still considering them equal.
     * @return the maximum units in last place value for the instance
     * @see Precision#equals(double, double, int)
     */
    public int getMaxUlps() {
        return maxUlps;
    }

    /** {@inheritDoc} **/
    @Override
    public boolean equals(final double a, final double b) {
        return Math.abs(a - b) <= eps || Precision.equals(a, b, maxUlps);
    }

    /** {@inheritDoc} **/
    @Override
    public int hashCode() {
        int result = 31;
        result += 17 * Double.hashCode(eps);
        result += 11 * Integer.hashCode(maxUlps);

        return result;
    }

    /** {@inheritDoc} **/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EpsilonUlpDoublePrecisionContext)) {
            return false;
        }

        EpsilonUlpDoublePrecisionContext other = (EpsilonUlpDoublePrecisionContext) obj;

        return this.eps == other.eps &&
                this.maxUlps == other.maxUlps;
    }

    /** {@inheritDoc} **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName())
            .append("[")
            .append("eps= ")
            .append(eps)
            .append(", maxUlps= ")
            .append(maxUlps)
            .append("]");

        return sb.toString();
    }
}
