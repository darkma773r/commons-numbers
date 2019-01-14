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

/** Simple {@link DoublePrecisonContext} subclass that uses an absolute epsilon value to
 * determine equality between doubles.
 *
 * <p>Two numbers are considered equal simply if they differ in numerical value by the epsilon
 * value or less. In other words, they are considered true if and only if the expression
 * {@code Math.abs(a - b) <= epsilon} evaluates to true.</p>
 */
public class EpsilonDoublePrecisionContext extends DoublePrecisionContext implements Serializable {

    /** Serializable identifer */
    private static final long serialVersionUID = 20181223L;

    /** Absolute epsilon value. */
    private final double epsilon;

    /** Simple constructor.
     * @param eps Absolute epsilon value. Numbers with a difference less than or equal
     *      to this value are considered equal.
     */
    public EpsilonDoublePrecisionContext(final double eps) {
        this.epsilon = eps;
    }

    /** Get the absolute epsilon value for the instance. Numbers with a difference less
     * than or equal to this value are considered equal.
     * @return the absolute epsilon value for the instance
     */
    public double getEpsilon() {
        return epsilon;
    }

    /** {@inheritDoc}
     * This value is equal to the epsilon value for the instance.
     * @see #getEpsilon()
     */
    @Override
    public double getZeroUpperBound() {
        return epsilon;
    }

    /** {@inheritDoc} **/
    @Override
    public boolean equals(final double a, final double b) {
        return Precision.equals(a, b, epsilon);
    }

    /** {@inheritDoc} **/
    @Override
    public int hashCode() {
        int result = 31;
        result += 17 * Double.hashCode(epsilon);

        return result;
    }

    /** {@inheritDoc} **/
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EpsilonDoublePrecisionContext)) {
            return false;
        }

        EpsilonDoublePrecisionContext other = (EpsilonDoublePrecisionContext) obj;

        return this.epsilon == other.epsilon;
    }

    /** {@inheritDoc} **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName())
            .append("[")
            .append("epsilon= ")
            .append(epsilon)
            .append("]");

        return sb.toString();
    }
}
