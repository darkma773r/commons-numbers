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
 * Execute benchmarks for {@link SafeNorm} methods.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-server", "-Xms512M", "-Xmx512M"})
public class EuclideanNormsPerformance {

    @State(Scope.Benchmark)
    public static class VectorArrayInput {

        private double[][] vectors;

        public double[][] getVectors() {
            return vectors;
        }

        @Setup
        public void setup() {
            final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP);
            vectors = new double[100_000][];
            for (int i = 0; i < vectors.length; ++i) {
                vectors[i] = randomDoubleArray(rng, 3);
            }
        }
    }

    /** Creates a random double number with a random sign and mantissa and a large range for
     * the exponent. The numbers will not be uniform over the range.
     * @param rng random number generator
     * @return the random number
     */
    private static double randomDouble(final UniformRandomProvider rng, final int maxExp, final int minExp) {
        // Create random doubles using random bits in the sign bit and the mantissa.
        // Then create an exponent in the range -64 to 64. Thus the sum product
        // of 4 max or min values will not over or underflow.
        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;
        // The exponent must be unsigned so + 1023 to the signed exponent
        final int expRange = Math.abs(maxExp - minExp);
        final long exp = rng.nextInt(expRange) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }

    /** Create an array of doubles populated using {@link #randomDouble(UniformRandomProvider)}.
     * @param rng uniform random provider
     * @param len array length
     * @return array containing {@code len} random doubles
     */
    private static double[] randomDoubleArray(final UniformRandomProvider rng, final int len) {
        final double[] arr = new double[len];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = randomDouble(rng, -550, +550);
        }

        return arr;
    }

    private static void eval(final ToDoubleFunction<double[]> fn, final VectorArrayInput input,
            final Blackhole bh) {
        final double[][] vectors = input.getVectors();
        for (int i = 0; i < vectors.length; ++i) {
            bh.consume(fn.applyAsDouble(vectors[i]));
        }
    }

    @Benchmark
    public void exact(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNorms.Exact(), input, bh);
    }

    @Benchmark
    public void direct(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNorms.Direct(), input, bh);
    }

    @Benchmark
    public void enorm(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNorms.Enorm(), input, bh);
    }

    @Benchmark
    public void enormMod(final VectorArrayInput input, final Blackhole bh) {
        eval(new EuclideanNorms.EnormMod(), input, bh);
    }
}
