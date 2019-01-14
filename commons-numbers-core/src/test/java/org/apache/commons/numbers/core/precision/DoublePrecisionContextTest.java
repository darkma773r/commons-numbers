package org.apache.commons.numbers.core.precision;

import org.junit.Assert;
import org.junit.Test;


public class DoublePrecisionContextTest {

    private static final double TEST_EPS = 0.5;

    @Test
    public void testEqualsZero() {
        // arrange
        StubContext ctx = new StubContext();

        // act/assert
        Assert.assertFalse(ctx.equalsZero(-1.0));
        Assert.assertFalse(ctx.equalsZero(-0.6));

        Assert.assertTrue(ctx.equalsZero(-0.5));
        Assert.assertTrue(ctx.equalsZero(-0.0));
        Assert.assertTrue(ctx.equalsZero(0.0));
        Assert.assertTrue(ctx.equalsZero(0.5));

        Assert.assertFalse(ctx.equalsZero(0.6));
        Assert.assertFalse(ctx.equalsZero(1.0));
    }

    @Test
    public void testCompare_primitive() {
        // arrange
        StubContext ctx = new StubContext();

        // act/assert
        Assert.assertEquals(0, ctx.compare(1, 1));
        Assert.assertEquals(-1, ctx.compare(1, 2));
        Assert.assertEquals(1, ctx.compare(2, 1));

        Assert.assertEquals(0, ctx.compare(-1, -1));
        Assert.assertEquals(1, ctx.compare(-1, -2));
        Assert.assertEquals(-1, ctx.compare(-2, -1));
    }

    @Test
    public void testCompare_wrapper() {
        // arrange
        StubContext ctx = new StubContext();

        // act/assert
        Assert.assertEquals(0, ctx.compare(new Double(1), new Double(1)));
        Assert.assertEquals(-1, ctx.compare(new Double(1), new Double(2)));
        Assert.assertEquals(1, ctx.compare(new Double(2), new Double(1)));

        Assert.assertEquals(0, ctx.compare(new Double(-1), new Double(-1)));
        Assert.assertEquals(1, ctx.compare(new Double(-1), new Double(-2)));
        Assert.assertEquals(-1, ctx.compare(new Double(-2), new Double(-1)));
    }

    private static class StubContext extends DoublePrecisionContext {

        @Override
        public boolean equals(double a, double b) {
            return Math.abs(a - b) <= TEST_EPS;
        }

        @Override
        public double getZeroUpperBound() {
            return TEST_EPS;
        }
    }
}
