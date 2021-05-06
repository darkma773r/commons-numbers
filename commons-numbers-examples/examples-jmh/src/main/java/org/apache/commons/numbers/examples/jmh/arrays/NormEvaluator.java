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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

/**
 * Class used to evaluate the accuracy of different norm computation
 * methods.
 */
public class NormEvaluator {

    /** Map of names to norm computation methods. */
    private final Map<String, ToDoubleFunction<double[]>> methods = new LinkedHashMap<>();

    private final int len;

    private final int minExp;

    private final int maxExp;

    private final Random rnd;

    NormEvaluator(final int len, final int minExp, final int maxExp, final Random rnd) {
        this.len = len;
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.rnd = rnd;
    }

    public NormEvaluator addMethod(final String name, final ToDoubleFunction<double[]> method) {
        methods.put(name, method);
        return this;
    }

    public NormEvaluator.Result evaluate(final int count) {

        final Map<String, NormStatsAccumulator> accumulators = new HashMap<>();
        for (final String name : methods.keySet()) {
            accumulators.put(name, new NormStatsAccumulator(count));
        }

        double[] vec;
        double exact;
        double sample;
        for (int i = 0; i < count; ++i) {
            vec = randomVector();

            exact = computeExact(vec);

            for (final Map.Entry<String, ToDoubleFunction<double[]>> entry : methods.entrySet()) {
                sample = entry.getValue().applyAsDouble(vec);

                accumulators.get(entry.getKey()).report(exact, sample);
            }
        }

        final Map<String, Stats> stats = new LinkedHashMap<>();
        for (final String name : methods.keySet()) {
            stats.put(name, accumulators.get(name).computeStats());
        }

        return new Result(count, len, minExp, maxExp, stats);
    }

    private double[] randomVector() {
        final double[] vec = new double[len];
        for (int i = 0; i < vec.length; ++i) {
            vec[i] = randomDouble();
        }
        return vec;
    }

    private double randomDouble() {
        // Create random doubles using random bits in the sign bit and the mantissa.
        // Then create an exponent in the range -64 to 64. Thus the sum product
        // of 4 max or min values will not over or underflow.
        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rnd.nextLong() & mask;
        // The exponent must be unsigned so + 1023 to the signed exponent
        final int expRange = Math.abs(maxExp - minExp);
        final long exp = rnd.nextInt(expRange) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }

    /** Compute the exact double value of the vector norm using BigDecimals
     * with a math context of {@link MathContext#DECIMAL128}.
     * @param vec
     * @return
     */
    private static double computeExact(final double[] vec) {
        final MathContext ctx = MathContext.DECIMAL128;

        BigDecimal sum = BigDecimal.ZERO;
        for (final double v : vec) {
            sum = sum.add(BigDecimal.valueOf(v).pow(2), ctx);
        }

        return sum.sqrt(ctx).doubleValue();
    }

    private static int computeUlpDifference(final double a, final double b) {
        return (int) (Double.doubleToLongBits(a) - Double.doubleToLongBits(b));
    }

    public static final class Result {

        private final int sampleCount;

        private final int inputLen;

        private final int minExp;

        private final int maxExp;

        private final Map<String, Stats> stats;

        Result(final int sampleCount, final int inputLen, final int minExp, final int maxExp,
                final Map<String, Stats> stats) {
            this.sampleCount = sampleCount;
            this.inputLen = inputLen;
            this.minExp = minExp;
            this.maxExp = maxExp;
            this.stats = Collections.unmodifiableMap(stats);
        }

        public int getSampleCount() {
            return sampleCount;
        }

        public int getInputLen() {
            return inputLen;
        }

        public int getMinExp() {
            return minExp;
        }

        public int getMaxExp() {
            return maxExp;
        }

        public Map<String, Stats> getStats() {
            return stats;
        }
    }

    public static final class Stats {

        private final double ulpErrorMean;

        private final double ulpErrorStdDev;

        private final int nonFiniteCount;

        Stats(final double ulpErrorMean, final double ulpErrorStdDev, final int nonFiniteCount) {
            this.ulpErrorMean = ulpErrorMean;
            this.ulpErrorStdDev = ulpErrorStdDev;
            this.nonFiniteCount = nonFiniteCount;
        }

        public double getUlpErrorMean() {
            return ulpErrorMean;
        }

        public double getUlpErrorStdDev() {
            return ulpErrorStdDev;
        }

        public int getNonFiniteCount() {
            return nonFiniteCount;
        }
    }

    private static final class NormStatsAccumulator {

        private int i;

        private final double[] ulpErrors;

        NormStatsAccumulator(final int count) {
            ulpErrors = new double[count];
        }

        public void report(final double expected, final double actual) {
            ulpErrors[i++] = Double.isFinite(actual) ?
                    computeUlpDifference(expected, actual) :
                    Double.NaN;
        }

        public Stats computeStats() {
            int finiteCount = 0;
            double sum = 0d;
            for (double ulpError : ulpErrors) {
                if (Double.isFinite(ulpError)) {
                    ++finiteCount;
                    sum += ulpError;
                }
            }

            final double mean = sum / (finiteCount);

            double diffSumSq = 0d;
            double diff;
            for (double ulpError : ulpErrors) {
                if (Double.isFinite(ulpError)) {
                    diff = ulpError - mean;
                    diffSumSq = diff * diff;
                }
            }

            final double stdDev = Math.sqrt(diffSumSq / (finiteCount - 1));

            return new Stats(mean, stdDev, ulpErrors.length - finiteCount);
        }
    }
}
