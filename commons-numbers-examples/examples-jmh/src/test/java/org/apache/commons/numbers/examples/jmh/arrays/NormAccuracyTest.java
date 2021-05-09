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
import java.util.Formatter;
import java.util.Map;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Test;

/**
 * Test the accuracy of various norm vector computation algorithms.
 */
public class NormAccuracyTest {

    /** Length of vectors to compute norms for. */
    private static final int VECTOR_LENGTH = 3;

    @Test
    //@Disabled("This method is used to output a report of the accuracy of implementations.")
    void reportUlpErrors() throws IOException {
        final UniformRandomProvider rng = RandomSource.create(RandomSource.XO_RO_SHI_RO_1024_PP);
        final int minExp = -500;
        final int maxExp = +550;
        final int samples = 100_000;
        final EuclideanNormEvaluator eval = new EuclideanNormEvaluator(VECTOR_LENGTH, minExp, maxExp, rng);
        eval.addMethod("exact", new EuclideanNorms.Exact())
            .addMethod("direct", new EuclideanNorms.Direct())
            .addMethod("enorm", new EuclideanNorms.Enorm())
            .addMethod("enormMod", new EuclideanNorms.EnormMod());

        final EuclideanNormEvaluator.Result result = eval.evaluate(samples);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("target/norms.csv"))) {
            writer.write("name, ulp error, std dev, non-finite");
            writer.newLine();

            EuclideanNormEvaluator.Stats stats;
            try (Formatter fmt = new Formatter(writer)) {
                for (Map.Entry<String, EuclideanNormEvaluator.Stats> entry : result.getStats().entrySet()) {
                    stats = entry.getValue();

                    writer.write(String.format("%s,%.3g,%.3g,%d",
                            entry.getKey(),
                            stats.getUlpErrorMean(),
                            stats.getUlpErrorStdDev(),
                            stats.getNonFiniteCount()));
                    writer.newLine();
                }
            }
        }
    }
}
