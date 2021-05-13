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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test the accuracy of the algorithms in the {@link EuclideanNorms} class.
 */
class EuclideanNormAccuracyTest {

    /** Length of vectors to compute norms for. */
    private static final int VECTOR_LENGTH = 3;

    /** Number of samples per evaluation. */
    private static final int SAMPLE_COUNT = 100_000;

    /** Report the relative error of various Euclidean norm computation methods and write
     * the results to a csv file. This is not a test.
     * @throws IOException if an I/O error occurs
     */
    @Test
    @Disabled("This method is used to output a report of the accuracy of implementations.")
    void reportUlpErrors() throws IOException {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("target/norms.csv"))) {
            writer.write("name, input type, ulp error, std dev, non-finite");
            writer.newLine();

            evaluate("high", +524, +550, rng, writer);
            evaluate("mid-high", +490, +510, rng, writer);
            evaluate("mid", -10, +10, rng, writer);
            evaluate("mid-low", -510, -490, rng, writer);
            evaluate("low", -550, -524, rng, writer);
            evaluate("full", -550, +550, rng, writer);
        }
    }

    /** Perform a single evaluation run and write the results to {@code writer}.
     * @param inputType type of evaluation input
     * @param minExp minimum double exponent
     * @param maxExp maximum double exponent
     * @param rng random number generator
     * @param writer output writer
     * @throws IOException if an I/O error occurs
     */
    private static void evaluate(final String inputType, final int minExp, final int maxExp,
            final UniformRandomProvider rng, final BufferedWriter writer) throws IOException {
        final EuclideanNormEvaluator eval = new EuclideanNormEvaluator(
                VECTOR_LENGTH, minExp, maxExp, rng);

        eval.addMethod("exact", new EuclideanNormAlgorithms.Exact())
            .addMethod("direct", new EuclideanNormAlgorithms.Direct())
            .addMethod("enorm", new EuclideanNormAlgorithms.Enorm())
            .addMethod("enormMod", new EuclideanNormAlgorithms.EnormMod());

        final EuclideanNormEvaluator.Result result = eval.evaluate(SAMPLE_COUNT);
        writeResults(inputType, result, writer);
    }

    /** Write evaluation results to the given writer instance.
     * @param inputType type of evaluation input
     * @param result evaluation result
     * @param writer writer instance
     * @throws IOException if an I/O error occurs
     */
    private static void writeResults(final String inputType, final EuclideanNormEvaluator.Result result,
            final BufferedWriter writer) throws IOException {
        for (Map.Entry<String, EuclideanNormEvaluator.Stats> entry : result.getStats().entrySet()) {
            EuclideanNormEvaluator.Stats stats = entry.getValue();

            writer.write(String.format("%s,%s,%.3g,%.3g,%d",
                    entry.getKey(),
                    inputType,
                    stats.getUlpErrorMean(),
                    stats.getUlpErrorStdDev(),
                    stats.getNonFiniteCount()));
            writer.newLine();
        }
    }
}
