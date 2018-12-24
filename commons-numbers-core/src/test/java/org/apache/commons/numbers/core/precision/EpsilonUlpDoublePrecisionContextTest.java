package org.apache.commons.numbers.core.precision;

import org.apache.commons.numbers.core.precision.EpsilonUlpDoublePrecisionContext;
import org.junit.Assert;
import org.junit.Test;

public class EpsilonUlpDoublePrecisionContextTest {

    @Test
    public void testProperties() {
        // act
        EpsilonUlpDoublePrecisionContext ctx = new EpsilonUlpDoublePrecisionContext(1e-6, 10);

        // assert
        Assert.assertEquals(ctx.getEps(), 1e-6, 0d);
        Assert.assertEquals(ctx.getMaxUlps(), 10);
    }

    @Test
    public void testEquals_epsAndUlps() {
        // arrange
        double eps = 1e-2;
        int maxUlps = 2;

        double tiny = 1e-3;
        double small = 1e-1;
        double big = 1e100;

        EpsilonUlpDoublePrecisionContext ctx = new EpsilonUlpDoublePrecisionContext(eps, maxUlps);

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
        Assert.assertTrue(ctx.equals(big, nextUp(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 3)));
    }

    @Test
    public void testEquals_epsOnly() {
        // arrange
        double eps = 1e-2;
        int maxUlps = 0;

        double tiny = 1e-3;
        double small = 1e-1;
        double big = 1e100;

        EpsilonUlpDoublePrecisionContext ctx = new EpsilonUlpDoublePrecisionContext(eps, maxUlps);

        // --- act/assert
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
        Assert.assertFalse(ctx.equals(big, nextUp(big, 1)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 3)));
    }

    @Test
    public void testEquals_ulpsOnly() {
        // arrange
        double eps = 0d;
        int maxUlps = 2;

        double tiny = 1e-3;
        double small = 1e-1;
        double big = 1e100;

        EpsilonUlpDoublePrecisionContext ctx = new EpsilonUlpDoublePrecisionContext(eps, maxUlps);

        // --- act/assert
        Assert.assertFalse(ctx.equals(0d, tiny));
        Assert.assertFalse(ctx.equals(0d, 2d * tiny));
        Assert.assertFalse(ctx.equals(0d, small));
        Assert.assertFalse(ctx.equals(0d, big));

        Assert.assertTrue(ctx.equals(small, small));
        Assert.assertFalse(ctx.equals(small, small + tiny));
        Assert.assertTrue(ctx.equals(small, nextUp(small, 2)));
        Assert.assertFalse(ctx.equals(small, nextUp(small, 3)));

        Assert.assertTrue(ctx.equals(big, big));
        Assert.assertTrue(ctx.equals(big, nextUp(big, 1)));
        Assert.assertTrue(ctx.equals(big, nextUp(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextUp(big, 3)));
    }

    @Test
    public void testEquals_negativeNumbers() {
        // arrange
        double eps = 1e-2;
        int maxUlps = 2;

        double tiny = -1e-3;
        double small = -1e-1;
        double big = -1e100;

        EpsilonUlpDoublePrecisionContext ctx = new EpsilonUlpDoublePrecisionContext(eps, maxUlps);

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
        Assert.assertTrue(ctx.equals(big, nextDown(big, 2)));
        Assert.assertFalse(ctx.equals(big, nextDown(big, 3)));
    }

    @Test
    public void testHashCode() {
        // arrange
        EpsilonUlpDoublePrecisionContext a = new EpsilonUlpDoublePrecisionContext(1e-6, 10);
        EpsilonUlpDoublePrecisionContext b = new EpsilonUlpDoublePrecisionContext(1e-6, 11);
        EpsilonUlpDoublePrecisionContext c = new EpsilonUlpDoublePrecisionContext(1e-7, 10);
        EpsilonUlpDoublePrecisionContext d = new EpsilonUlpDoublePrecisionContext(1e-6, 10);

        // act/assert
        Assert.assertEquals(a.hashCode(), a.hashCode());
        Assert.assertEquals(a.hashCode(), d.hashCode());

        Assert.assertNotEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    public void testEquals() {
        // arrange
        EpsilonUlpDoublePrecisionContext a = new EpsilonUlpDoublePrecisionContext(1e-6, 10);
        EpsilonUlpDoublePrecisionContext b = new EpsilonUlpDoublePrecisionContext(1e-6, 11);
        EpsilonUlpDoublePrecisionContext c = new EpsilonUlpDoublePrecisionContext(1e-7, 10);
        EpsilonUlpDoublePrecisionContext d = new EpsilonUlpDoublePrecisionContext(1e-6, 10);

        // act/assert
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals(new Object()));
        Assert.assertFalse(a.equals(b));
        Assert.assertFalse(b.equals(a));
        Assert.assertFalse(a.equals(c));

        Assert.assertTrue(a.equals(a));
        Assert.assertTrue(a.equals(d));
    }

    @Test
    public void testToString() {
        // arrange
        EpsilonUlpDoublePrecisionContext a = new EpsilonUlpDoublePrecisionContext(1d, 2);

        // act
        String str = a.toString();

        // assert
        Assert.assertTrue(str.contains("EpsilonUlpDoublePrecisionContext"));
        Assert.assertTrue(str.contains("eps= 1"));
        Assert.assertTrue(str.contains("maxUlps= 2"));
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
