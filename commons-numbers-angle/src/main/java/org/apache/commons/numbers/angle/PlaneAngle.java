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

import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;

public abstract class PlaneAngle {

    private static final double TURNS_TO_DEGREES = 360;
    private static final double TURNS_TO_RADIANS = 2 * Math.PI;

    private static final double DEGREES_TO_TURNS = 1d / TURNS_TO_DEGREES;
    private static final double DEGREES_TO_RADIANS = Math.PI / 180;

    private static final double RADIANS_TO_TURNS = 1d / TURNS_TO_RADIANS;
    private static final double RADIANS_TO_DEGREES = 180 / Math.PI;

    private final double value;

    private PlaneAngle(final double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public abstract double turns();

    public abstract double degrees();

    public abstract double radians();

    public abstract PlaneAngle addPeriod(double factor);

    public PlaneAngle add(final PlaneAngle other) {
        return getPreferredUnitOperations(this, other).add(this, other);
    }

    public PlaneAngle subtract(final PlaneAngle other) {
        return getPreferredUnitOperations(this, other).subtract(this, other);
    }

    public abstract PlaneAngle multiply(final double x);

    public PlaneAngle normalizeAbove(final PlaneAngle lowerBound) {
        return null;
    }

    public UnaryOperator<PlaneAngle> normalizerForLowerBound(final PlaneAngle lowerBound) {
        // TODO
        return null;
    }

    public Turns toTurns() {
        return new Turns(turns());
    }

    public Degrees toDegrees() {
        return new Degrees(degrees());
    }

    public Radians toRadians() {
        return new Radians(radians());
    }

    abstract UnitOperations<?> getUnitOperations();

    public static Turns ofTurns(final double turns) {
        return new Turns(turns);
    }

    public static Degrees ofDegrees(final double deg) {
        return new Degrees(deg);
    }

    public static Radians ofRadians(final double rad) {
        return new Radians(rad);
    }

    private static UnitOperations<?> getPreferredUnitOperations(final PlaneAngle a, final PlaneAngle b) {
        final UnitOperations<?> aOps = a.getUnitOperations();
        final UnitOperations<?> bOps = a.getUnitOperations();

        return aOps.priority > bOps.priority ? aOps : bOps;
    }

    public static final class Turns extends PlaneAngle {

        private static final UnitOperations<Turns> OPS = new UnitOperations<>(0, 1, PlaneAngle::turns, Turns::new);

        private Turns(final double value) {
            super(value);
        }

        /** {@inheritDoc} */
        @Override
        public double turns() {
            return getValue();
        }

        /** {@inheritDoc} */
        @Override
        public double degrees() {
            return getValue() * TURNS_TO_DEGREES;
        }

        /** {@inheritDoc} */
        @Override
        public double radians() {
            return getValue() * TURNS_TO_RADIANS;
        }

        /** {@inheritDoc} */
        @Override
        public Turns addPeriod(final double factor) {
            return getUnitOperations().addPeriod(this, factor);
        }

        public Turns add(final Turns other) {
            return getUnitOperations().add(this, other);
        }

        public Turns subtract(final Turns other) {
            return getUnitOperations().subtract(this, other);
        }

        /** {@inheritDoc} */
        @Override
        public Turns multiply(final double x) {
            return getUnitOperations().multiply(this, x);
        }

        /** {@inheritDoc} */
        @Override
        public Turns toTurns() {
            return this;
        }

        /** {@inheritDoc} */
        @Override
        UnitOperations<Turns> getUnitOperations() {
            return OPS;
        }
    }

    public static final class Degrees extends PlaneAngle {

        private static final UnitOperations<Degrees> OPS =
                new UnitOperations<>(1, 360, PlaneAngle::degrees, Degrees::new);

        private Degrees(final double value) {
            super(value);
        }

        /** {@inheritDoc} */
        @Override
        public double turns() {
            return getValue() * DEGREES_TO_TURNS;
        }

        /** {@inheritDoc} */
        @Override
        public double degrees() {
            return getValue();
        }

        /** {@inheritDoc} */
        @Override
        public double radians() {
            return getValue() * DEGREES_TO_RADIANS;
        }

        /** {@inheritDoc} */
        @Override
        public Degrees addPeriod(final double factor) {
            return getUnitOperations().addPeriod(this, factor);
        }

        public Degrees add(final Degrees other) {
            return getUnitOperations().add(this, other);
        }

        public Degrees subtract(final Degrees other) {
            return getUnitOperations().subtract(this, other);
        }

        /** {@inheritDoc} */
        @Override
        public Degrees multiply(final double x) {
            return getUnitOperations().multiply(this, x);
        }

        /** {@inheritDoc} */
        @Override
        public Degrees toDegrees() {
            return this;
        }

        /** {@inheritDoc} */
        @Override
        UnitOperations<Degrees> getUnitOperations() {
            return OPS;
        }
    }

    public static final class Radians extends PlaneAngle {

        public static final Radians ZERO = new Radians(0);

        public static final Radians PI = new Radians(Math.PI);

        public static final Radians TWO_PI = new Radians(2 * Math.PI);

        private static final UnitOperations<Radians> OPS =
                new UnitOperations<>(2, 2 * Math.PI, PlaneAngle::radians, Radians::new);

        private Radians(final double value) {
            super(value);
        }

        /** {@inheritDoc} */
        @Override
        public double turns() {
            return getValue() * RADIANS_TO_TURNS;
        }

        /** {@inheritDoc} */
        @Override
        public double degrees() {
            return getValue() * RADIANS_TO_DEGREES;
        }

        /** {@inheritDoc} */
        @Override
        public double radians() {
            return getValue();
        }

        /** {@inheritDoc} */
        @Override
        public Radians addPeriod(final double factor) {
            return getUnitOperations().addPeriod(this, factor);
        }

        public Radians add(final Radians other) {
            return getUnitOperations().add(this, other);
        }

        public Radians subtract(final Radians other) {
            return getUnitOperations().subtract(this, other);
        }

        /** {@inheritDoc} */
        @Override
        public Radians multiply(final double x) {
            return getUnitOperations().multiply(this, x);
        }

        /** {@inheritDoc} */
        @Override
        public Radians toRadians() {
            return this;
        }

        /** {@inheritDoc} */
        @Override
        UnitOperations<Radians> getUnitOperations() {
            return OPS;
        }
    }

    private static final class UnitOperations<A extends PlaneAngle> {
        private final int priority;
        private final double period;
        private final ToDoubleFunction<PlaneAngle> valueFn;
        private final DoubleFunction<A> factory;

        UnitOperations(final int priority, final double period, final ToDoubleFunction<PlaneAngle> valueFn,
                final DoubleFunction<A> factory) {
            this.priority = priority;
            this.period = period;
            this.valueFn = valueFn;
            this.factory = factory;
        }

        A addPeriod(final PlaneAngle a, final double factor) {
            final double aValue = getValue(a);

            return createAngle(aValue + (factor * period));
        }

        A add(final PlaneAngle a, final PlaneAngle b) {
            final double aValue = getValue(a);
            final double bValue = getValue(b);

            return factory.apply(aValue + bValue);
        }

        A subtract(final PlaneAngle a, final PlaneAngle b) {
            final double aValue = getValue(a);
            final double bValue = getValue(b);

            return createAngle(aValue - bValue);
        }

        A multiply(final PlaneAngle a, final double x) {
            final double aValue = getValue(a);

            return createAngle(aValue * x);
        }

        double getValue(final PlaneAngle angle) {
            return valueFn.applyAsDouble(angle);
        }

        A createAngle(final double value) {
            return factory.apply(value);
        }
    }

    private static final class Normalizer implements UnaryOperator<PlaneAngle> {

        private final UnitOperations<?> ops;

        private final double lowerBound;

        private final double upperBound;

        private final Reduce reduce;

        Normalizer(final UnitOperations<?> ops, final double lowerBound, final double upperBound) {
            this.ops = ops;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.reduce = new Reduce(lowerBound, ops.period);
        }

        /** {@inheritDoc} */
        @Override
        public PlaneAngle apply(final PlaneAngle angle) {
            final double value = ops.getValue(angle);
            final double normalized = getNormalizedValue(value);

            return ops.createAngle(normalized);
        }

        private double getNormalizedValue(final double value) {
            if (value < lowerBound || value >= upperBound) {
                final double normalized = reduce.applyAsDouble(value) + lowerBound;
                return normalized < upperBound ?
                    normalized :
                    // If value is too small to be representable compared to the
                    // floor expression above (ie, if value + x = x), then we may
                    // end up with a number exactly equal to the upper bound here.
                    // In that case, return the lower bound so we can fulfill the
                    // contract of only returning results strictly less than the
                    // upper bound.
                    lowerBound;
            }

            return value;
        }
    }
}
