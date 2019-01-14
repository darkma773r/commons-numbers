package org.apache.commons.numbers.core.precision;

import org.apache.commons.numbers.core.precision.EpsilonDoublePrecisionContext;
import org.junit.Assert;
import org.junit.Test;

public class EpsilonDoublePrecisionContextTest {

    @Test
    public void testProperties() {
        // act
        EpsilonDoublePrecisionContext ctx = new EpsilonDoublePrecisionContext(1e-6);

        // assert
        Assert.assertEquals(ctx.getEpsilon(), 1e-6, 0d);
    }

    @Test
    public void testEquals_positiveNumbers() {
        // arrange
        double eps = 1e-2;

        double tiny = 1e-3;
        double small = 1e-1;
        double big = 1e100;

        EpsilonDoublePrecisionContext ctx = new EpsilonDoublePrecisionContext(eps);

        // act/assert
        Assert.assertTrue(ctx.equals(0d, tiny));
        Assert.assertTrue(ctx.equals(0d, 2d * tiny));
        Assert.assertFalse(ctx.equals(0d, small));
        Assert.assertFalse(ctx.equals(0d, big));

        Assert.assertTrue(ctx.equals(small, small));
        Assert.assertTrue(ctx.equals(small, small + tiny));
        Assert.assertTrue(ctx.equals(small, nextUp(small, 2)));
        Assert.assertTrue(ctx.equals(small, nextUp(small, 3)));
        Assert.assertFalse(ctx.equals(small, small + nextUp(eps, 1)));

        Assert.assertTrue(ctx.equals(big, big));
        Assert.assertTrue(ctx.equals(big, nextUp(big, 1)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 3)));
    }

    @Test
    public void testEquals_negativeNumbers() {
        // arrange
        double eps = 1e-2;

        double tiny = -1e-3;
        double small = -1e-1;
        double big = -1e100;

        EpsilonDoublePrecisionContext ctx = new EpsilonDoublePrecisionContext(eps);

        // act/assert
        Assert.assertTrue(ctx.equals(0d, tiny));
        Assert.assertTrue(ctx.equals(0d, 2d * tiny));
        Assert.assertFalse(ctx.equals(0d, small));
        Assert.assertFalse(ctx.equals(0d, big));

        Assert.assertTrue(ctx.equals(small, small));
        Assert.assertTrue(ctx.equals(small, small - tiny));
        Assert.assertTrue(ctx.equals(small, nextDown(small, 2)));
        Assert.assertTrue(ctx.equals(small, nextDown(small, 3)));
        Assert.assertFalse(ctx.equals(small, small - nextUp(eps, 1)));

        Assert.assertTrue(ctx.equals(big, big));
        Assert.assertTrue(ctx.equals(big, nextDown(big, 1)));
        Assert.assertFalse(ctx.equals(big, nextDown(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextDown(big, 3)));
    }

    @Test
    public void testEquals_NaN() {
        // arrange
        EpsilonDoublePrecisionContext ctx = new EpsilonDoublePrecisionContext(1e-3);

        // act/assert
        Assert.assertFalse(ctx.equals(Double.NaN, Double.NaN));
    }

    @Test
    public void testEquals_infinity() {
        // arrange
        EpsilonDoublePrecisionContext ctx = new EpsilonDoublePrecisionContext(1e-3);

        // act/assert
        Assert.assertTrue(ctx.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        Assert.assertTrue(ctx.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));

        Assert.assertFalse(ctx.equals(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testHashCode() {
        // arrange
        EpsilonDoublePrecisionContext a = new EpsilonDoublePrecisionContext(1e-6);
        EpsilonDoublePrecisionContext b = new EpsilonDoublePrecisionContext(1e-7);
        EpsilonDoublePrecisionContext c = new EpsilonDoublePrecisionContext(1e-6);

        // act/assert
        Assert.assertEquals(a.hashCode(), a.hashCode());
        Assert.assertEquals(a.hashCode(), c.hashCode());

        Assert.assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testEquals() {
        // arrange
        EpsilonDoublePrecisionContext a = new EpsilonDoublePrecisionContext(1e-6);
        EpsilonDoublePrecisionContext b = new EpsilonDoublePrecisionContext(1e-7);
        EpsilonDoublePrecisionContext c = new EpsilonDoublePrecisionContext(1e-6);

        // act/assert
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals(new Object()));
        Assert.assertFalse(a.equals(b));
        Assert.assertFalse(b.equals(a));

        Assert.assertTrue(a.equals(a));
        Assert.assertTrue(a.equals(c));
    }

    @Test
    public void testToString() {
        // arrange
        EpsilonDoublePrecisionContext a = new EpsilonDoublePrecisionContext(1d);

        // act
        String str = a.toString();

        // assert
        Assert.assertTrue(str.contains("EpsilonDoublePrecisionContext"));
        Assert.assertTrue(str.contains("epsilon= 1"));
    }

    /**
     * Increments the given double value {@code count} number of times
     * using {@link Math#nextUp(double)}.
     * @param n
     * @param count
     * @return
     */
    private static double nextUp(double n, int count) {
        double result = n;
        for (int i=0; i<count; ++i) {
            result = Math.nextUp(result);
        }

        return result;
    }

    /**
     * Decrements the given double value {@code count} number of times
     * using {@link Math#nextDown(double)}.
     * @param n
     * @param count
     * @return
     */
    private static double nextDown(double n, int count) {
        double result = n;
        for (int i=0; i<count; ++i) {
            result = Math.nextDown(result);
        }

        return result;
    }
}
