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
import java.util.function.ToDoubleFunction;

import org.apache.commons.rng.UniformRandomProvider;

/**
 * Class used to evaluate the accuracy of different norm computation
 * methods.
 */
public class EuclideanNormEvaluator {
    /** Vector array length. */
    private final int len;

    /** Minimum double exponent. */
    private final int minExp;

    /** Maximum double exponent. */
    private final int maxExp;

    /** Random number generator. */
    private final UniformRandomProvider rng;

    /** Map of names to norm computation methods. */
    private final Map<String, ToDoubleFunction<double[]>> methods = new LinkedHashMap<>();

    /** Construct a new evaluator instance.
     * @param len vector array length
     * @param minExp minimum double exponent
     * @param maxExp maximum double exponent
     * @param rng random number generator
     */
    EuclideanNormEvaluator(final int len, final int minExp, final int maxExp, final UniformRandomProvider rng) {
        this.len = len;
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.rng = rng;
    }

    /** Add a computation method to be evaluated.
     * @param name method name
     * @param method computation method
     * @return this instance
     */
    public EuclideanNormEvaluator addMethod(final String name, final ToDoubleFunction<double[]> method) {
        methods.put(name, method);
        return this;
    }

    /** Evaluate the configured computation methods, using {@code count} number of random vectors.
     * @param count number of random vectors to use in the computation
     * @return evaluation result
     */
    public EuclideanNormEvaluator.Result evaluate(final int count) {

        final Map<String, StatsAccumulator> accumulators = new HashMap<>();
        for (final String name : methods.keySet()) {
            accumulators.put(name, new StatsAccumulator(count));
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

    /** Generate a random vector using the configuration of the instance.
     * @return random vector array of length {@code len}
     */
    private double[] randomVector() {
        final double[] vec = new double[len];
        for (int i = 0; i < vec.length; ++i) {
            vec[i] = randomDouble();
        }
        return vec;
    }

    /** Generate a random double value using the configuration of the instance.
     * @return random double value
     */
    private double randomDouble() {
        // Create random doubles using random bits in the sign bit and the mantissa.
        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;
        // The exponent must be unsigned so + 1023 to the signed exponent
        final long exp = rng.nextInt(maxExp - minExp) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }

    /** Compute the exact double value of the vector norm using BigDecimals
     * with a math context of {@link MathContext#DECIMAL128}.
     * @param vec input vector
     * @return euclidean norm
     */
    private static double computeExact(final double[] vec) {
        final MathContext ctx = MathContext.DECIMAL128;

        BigDecimal sum = BigDecimal.ZERO;
        for (final double v : vec) {
            sum = sum.add(BigDecimal.valueOf(v).pow(2), ctx);
        }

        return sum.sqrt(ctx).doubleValue();
    }

    /** Compute the ulp difference between two values. This method assumes that
     * the arguments have the same exponent.
     * @param a first input
     * @param b second input
     * @return ulp difference between the arguments
     */
    private static int computeUlpDifference(final double a, final double b) {
        return (int) (Double.doubleToLongBits(a) - Double.doubleToLongBits(b));
    }

    /** Class containing the result of a norm evaluation.
     */
    public static final class Result {

        /** Number of vectors in the evaluation. */
        private final int sampleCount;

        /** Vector array length. */
        private final int vectorLen;

        /** Minimum double exponent value. */
        private final int minExp;

        /** Maximum double exponent value. */
        private final int maxExp;

        /** Stats by method name. */
        private final Map<String, Stats> stats;

        /** Construct a new result.
         * @param sampleCount number of samples
         * @param vectorLen vector array length
         * @param minExp minimum exponent
         * @param maxExp maximum exponent
         * @param stats state by method name
         */
        Result(final int sampleCount, final int vectorLen, final int minExp, final int maxExp,
                final Map<String, Stats> stats) {
            this.sampleCount = sampleCount;
            this.vectorLen = vectorLen;
            this.minExp = minExp;
            this.maxExp = maxExp;
            this.stats = Collections.unmodifiableMap(stats);
        }

        /** Get the number of vectors used in the evaluation.
         * @return number of vectors used in the evaluation
         */
        public int getSampleCount() {
            return sampleCount;
        }

        /** Get the vector array length.
         * @return vector array length
         */
        public int getVectorLen() {
            return vectorLen;
        }

        /** Get the minimum double exponent.
         * @return minimum double exponent
         */
        public int getMinExp() {
            return minExp;
        }

        /** Get the maximum double exponent.
         * @return maximum double exponent
         */
        public int getMaxExp() {
            return maxExp;
        }

        /** Get the map containing statistics for each evaluated
         * method.
         * @return map containing stats keyed by method name
         */
        public Map<String, Stats> getStats() {
            return stats;
        }
    }

    /** Class containing evaluation statistics for a single computation method.
     */
    public static final class Stats {

        /** Mean ulp error. */
        private final double ulpErrorMean;

        /** Ulp error standard deviation. */
        private final double ulpErrorStdDev;

        /** Number of non-finite computation results. */
        private final int nonFiniteCount;

        /** Construct a new instance.
         * @param ulpErrorMean ulp error mean
         * @param ulpErrorStdDev ulp error standard deviation
         * @param nonFiniteCount number of non-finite computation results
         */
        Stats(final double ulpErrorMean, final double ulpErrorStdDev, final int nonFiniteCount) {
            this.ulpErrorMean = ulpErrorMean;
            this.ulpErrorStdDev = ulpErrorStdDev;
            this.nonFiniteCount = nonFiniteCount;
        }

        /** Get the ulp error mean.
         * @return ulp error mean
         */
        public double getUlpErrorMean() {
            return ulpErrorMean;
        }

        /** Get the ulp error standard deviation.
         * @return ulp error standard deviation
         */
        public double getUlpErrorStdDev() {
            return ulpErrorStdDev;
        }

        /** Get the number of non-finite computation results.
         * @return number of non-finite computation results
         */
        public int getNonFiniteCount() {
            return nonFiniteCount;
        }
    }

    /** Class used to accumulate statistics during a norm evaluation run.
     */
    private static final class StatsAccumulator {

        /** Sample index. */
        private int sampleIdx;

        /** Array of ulp errors for each sample. */
        private final double[] ulpErrors;

        /** Construct a new instance.
         * @param count number of samples to be accumulated
         */
        StatsAccumulator(final int count) {
            ulpErrors = new double[count];
        }

        /** Report a computation result.
         * @param expected expected result
         * @param actual actual result
         */
        public void report(final double expected, final double actual) {
            ulpErrors[sampleIdx++] = Double.isFinite(actual) ?
                    computeUlpDifference(expected, actual) :
                    Double.NaN;
        }

        /** Compute the final statistics for the run.
         * @return statistics object
         */
        public Stats computeStats() {
            int finiteCount = 0;
            double sum = 0d;
            for (double ulpError : ulpErrors) {
                if (Double.isFinite(ulpError)) {
                    ++finiteCount;
                    sum += ulpError;
                }
            }

            final double mean = sum / finiteCount;

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
