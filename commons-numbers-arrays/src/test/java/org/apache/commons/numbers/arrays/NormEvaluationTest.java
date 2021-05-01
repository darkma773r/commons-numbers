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
package org.apache.commons.numbers.arrays;

import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.junit.jupiter.api.Test;

/**
 * Test used to evaluate different norm computation methods.
 */
class NormEvaluationTest {

    private static final ToDoubleFunction<double[]> DIRECT_NORM = vec -> {
        double sumSq = 0d;
        for (int i = 0; i < vec.length; ++i) {
            sumSq += vec[i] * vec[i];
        }

        return Math.sqrt(sumSq);
    };

    @Test
    void evaluate() {
        final Random rnd = new Random(1L);
        final NormEvaluator eval = new NormEvaluator(3, -300, +300, rnd);
        eval.addMethod("direct", DIRECT_NORM)
            .addMethod("safeNorm", SafeNorm::value);

        final NormEvaluator.Result result = eval.evaluate(1000);

        printResults(result);
    }

    private static void printResults(final NormEvaluator.Result result) {
        System.out.println("### Norm evaluation result");
        System.out.println("Sample count: " + result.getSampleCount());
        System.out.println("Input length: " + result.getInputLen());
        System.out.println("Min exp: " + result.getMinExp());
        System.out.println("Max exp: " + result.getMaxExp());
        System.out.println("| Name | ulp error mean | url error std dev |");
        for (final Map.Entry<String, NormEvaluator.Stats> entry : result.getStats().entrySet()) {
            System.out.println("| " + entry.getKey() + " | " +
                    entry.getValue().getUlpErrorMean() + " | " +
                    entry.getValue().getUlpErrorStdDev() + " |");
        }
    }
}
