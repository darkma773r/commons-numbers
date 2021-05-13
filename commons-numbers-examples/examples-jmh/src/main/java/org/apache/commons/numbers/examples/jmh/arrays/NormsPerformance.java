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
package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import org.apache.commons.numbers.arrays.Norms;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Execute benchmarks for the methods in the {@link Norms} class.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class NormsPerformance {

    /** Number of samples used in each benchmark. */
    private static final int SAMPLES = 100_000;

    /** Minimum random double exponent. */
    private static final int MIN_EXP = -550;

    /** Maximum random double exponent. */
    private static final int MAX_EXP = +550;

    /** Range of exponents within a single vector. */
    private static final int VECTOR_EXP_RANGE = 26;

    /** Class providing input vectors for benchmarks.
     */
    @State(Scope.Benchmark)
    public static class VectorArrayInput {

        /** Array of input vectors. */
        private double[][] vectors;

        /** Get the input vectors.
         * @return input vectors
         */
        public double[][] getVectors() {
            return vectors;
        }

        /** Create the input vectors for the instance.
         */
        @Setup
        public void createVectors() {
            final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP);

            vectors = new double[SAMPLES][];
            for (int i = 0; i < vectors.length; ++i) {
                final double[] v = new double[getLength()];

                // pick a general range for the vector element exponents and then
                // create values within that range
                final int vMidExp = rng.nextInt(MAX_EXP - MIN_EXP) + MIN_EXP;
                final int vExpRadius = VECTOR_EXP_RANGE / 2;
                final int vMinExp = vMidExp - vExpRadius;
                final int vMaxExp = vMidExp + vExpRadius;

                for (int j = 0; j < v.length; ++j) {
                    v[j] = randomDouble(vMinExp, vMaxExp, rng);
                }
                vectors[i] = v;
            }
        }

        /** Get the length of the input vectors.
         * @return input vector length
         */
        protected int getLength() {
            return 3;
        }
    }

    /** Class providing 2D input vectors for benchmarks.
     */
    @State(Scope.Benchmark)
    public static class VectorArrayInput2D extends VectorArrayInput {

        /** {@inheritDoc} */
        @Override
        protected int getLength() {
            return 2;
        }
    }

    /** Create a random double value with exponent in the range {@code [minExp, maxExp]}.
     * @param minExp minimum exponent value
     * @param maxExp maximum exponent value
     * @param rng random number generator
     * @return random double
     */
    private static double randomDouble(final int minExp, final int maxExp, final UniformRandomProvider rng) {
        // Create random doubles using random bits in the sign bit and the mantissa.
        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;
        // The exponent must be unsigned so + 1023 to the signed exponent
        final long exp = rng.nextInt(Math.abs(maxExp - minExp)) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }

    /** Evaluate a norm computation method with the given input.
     * @param fn function to evaluate
     * @param input computation input
     * @param bh blackhole
     */
    private static void eval(final ToDoubleFunction<double[]> fn, final VectorArrayInput input,
            final Blackhole bh) {
        final double[][] vectors = input.getVectors();
        for (int i = 0; i < vectors.length; ++i) {
            bh.consume(fn.applyAsDouble(vectors[i]));
        }
    }

    /** Compute a baseline performance metric with a method that does nothing.
     * @param input benchmark input
     * @param bh blackhole
     */
    @Benchmark
    public void baseline(final VectorArrayInput input, final Blackhole bh) {
        eval(v -> 0d, input, bh);
    }

    /** Compute a baseline performance metric using {@link Math#hypot(double, double)}.
     * @param input benchmark input
     * @param bh blackhole
     */
    @Benchmark
    public void hypot(final VectorArrayInput2D input, final Blackhole bh) {
        eval(v -> Math.hypot(v[0], v[1]), input, bh);
    }

    /** Compute the performance of the {@link Norms#euclidean(double, double)} method.
     * @param input benchmark input
     * @param bh blackhole
     */
    @Benchmark
    public void euclidean2d(final VectorArrayInput2D input, final Blackhole bh) {
        eval(v -> Norms.euclidean(v[0], v[1]), input, bh);
    }

    /** Compute the performance of the {@link Norms#euclidean(double, double, double)} method.
     * @param input benchmark input
     * @param bh blackhole
     */
    @Benchmark
    public void euclidean3d(final VectorArrayInput input, final Blackhole bh) {
        eval(v -> Norms.euclidean(v[0], v[1], v[2]), input, bh);
    }

    /** Compute the performance of the {@link Norms#euclidean(double[])} method.
     * @param input benchmark input
     * @param bh blackhole
     */
    @Benchmark
    public void euclideanArray(final VectorArrayInput input, final Blackhole bh) {
        eval(Norms::euclidean, input, bh);
    }
}
