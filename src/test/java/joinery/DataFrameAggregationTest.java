/*
 * Joinery -- Data frames for Java
 * Copyright (c) 2014, 2015 IBM Corp.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package joinery;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import joinery.impl.Aggregation;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class DataFrameAggregationTest {
    DataFrame<Object> df;

    @Before
    public void setUp()
            throws Exception {
        df = DataFrame.readCsv(ClassLoader.getSystemResourceAsStream("grouping.csv"));
    }

    @Test
    public void testSum() {
        assertArrayEquals(
                new Double[]{280.0, 280.0},
                df.sum().toArray()
        );
    }

    @Test
    public void testNullSortingAsc() {
        DataFrame ndf = ndf();
        ndf = ndf.sortBy(2);
        assertNull(ndf.row(0).get(2));
        assertNotNull(ndf.row(2).get(2));
    }

    @Test
    public void testNullSortingDesc() {
        DataFrame ndf = ndf();
        ndf = ndf.sortBy(-2);
        assertEquals(10_000.0d, (double) ndf.row(0).get(2), 0.001);
        assertNull(ndf.row(6).get(2));
    }

    private DataFrame ndf() {
        DataFrame ndf = new DataFrame();
        ndf.add("a", Arrays.asList("alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf"));
        ndf.add("b", Arrays.asList("one", "one", "two", "two", "three", "three", "three"));
        ndf.add("c", Arrays.asList(null, 100.0d, -1000.0d, 10.0d, null, 200.0d, 10_000d));
        return ndf;
    }

    @Test
    public void testMean() {
        assertArrayEquals(
                new Double[]{40.0, 40.0},
                df.mean().toArray()
        );
    }

    @Test
    public void testStd() {
        assertArrayEquals(
                new double[]{21.6024, 21.6024},
                df.stddev().toArray(double[].class),
                0.0001
        );
    }

    @Test
    public void testVar() {
        assertArrayEquals(
                new double[]{466.6666, 466.6666},
                df.var().toArray(double[].class),
                0.0001
        );
    }

    @Test
    public void testSkew() {
        assertArrayEquals(
                new Double[]{0.0, 0.0},
                df.skew().toArray()
        );
    }

    @Test
    public void testKurt() {
        assertArrayEquals(
                new Double[]{-1.2, -1.2},
                df.kurt().toArray()
        );
    }

    @Test
    public void testMin() {
        assertArrayEquals(
                new Double[]{10.0, 10.0},
                df.min().toArray()
        );
    }

    @Test
    public void testMax() {
        assertArrayEquals(
                new Double[]{70.0, 70.0},
                df.max().toArray()
        );
    }

    @Test
    public void testMedian() {
        assertArrayEquals(
                new Double[]{40.0, 40.0},
                df.median().toArray()
        );
    }

    @Test
    public void testCumsum() {
        assertArrayEquals(
                new Double[]{
                        10.0, 30.0, 60.0, 100.0, 150.0, 210.0, 280.0,
                        10.0, 30.0, 60.0, 100.0, 150.0, 210.0, 280.0
                },
                df.cumsum().toArray()
        );
    }

    @Test
    public void testCumsumGrouped() {
        assertArrayEquals(
                new Object[]{
                        "one", "one", "two", "two", "three", "three", "three",
                        10.0, 30.0, 30.0, 70.0, 50.0, 110.0, 180.0,
                        10.0, 30.0, 30.0, 70.0, 50.0, 110.0, 180.0
                },
                df.groupBy("b").cumsum().toArray()
        );
    }

    @Test
    public void testCumprod() {
        assertArrayEquals(
                new Double[]{
                        10.0, 200.0, 6000.0, 240000.0, 12000000.0, 720000000.0, 50400000000.0,
                        10.0, 200.0, 6000.0, 240000.0, 12000000.0, 720000000.0, 50400000000.0
                },
                df.cumprod().toArray()
        );
    }

    @Test
    public void testCummin() {
        df.set(4, 2, 1);
        assertArrayEquals(
                new Double[]{
                        10.0, 10.0, 10.0, 10.0, 1.0, 1.0, 1.0,
                        10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0
                },
                df.cummin().toArray()
        );
    }

    @Test
    public void testCummax() {
        df.set(4, 2, 100);
        assertArrayEquals(
                new Double[]{
                        10.0, 20.0, 30.0, 40.0, 100.0, 100.0, 100.0,
                        10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0
                },
                df.cummax().toArray()
        );
    }

    @Test
    public void testPercentile() {
        assertArrayEquals(
                new Double[]{60.0, 60.0},
                df.percentile(75).toArray()
        );
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testPercentileInvalid() {
        df.percentile(101);
    }

    @Test
    public void testDescribe() {
        assertArrayEquals(
                new double[]{
                        7.0, 40.0, 21.6024, 466.6667, 70.0, 10.0,
                        7.0, 40.0, 21.6024, 466.6667, 70.0, 10.0
                },
                df.describe().toArray(double[].class),
                0.0001
        );
    }

    @Test
    public void testDescribeGrouped() {
        assertArrayEquals(
                new double[]{
                        2.00000000, 15.00000000, 7.07106781, 50.00000000, 20.00000000, 10.00000000,
                        2.00000000, 35.00000000, 7.07106781, 50.00000000, 40.00000000, 30.00000000,
                        3.00000000, 60.00000000, 10.00000000, 100.00000000, 70.00000000, 50.00000000,
                        2.00000000, 15.00000000, 7.07106781, 50.00000000, 20.00000000, 10.00000000,
                        2.00000000, 35.00000000, 7.07106781, 50.00000000, 40.00000000, 30.00000000,
                        3.00000000, 60.00000000, 10.00000000, 100.00000000, 70.00000000, 50.00000000
                },
                df.groupBy("b").describe().toArray(double[].class),
                0.0001
        );
    }

    @Test
    public void testCov() {
        assertArrayEquals(
                new double[]{466.66667, 466.66667, 466.66667, 466.66667},
                df.cov().toArray(double[].class),
                0.0001
        );
    }

    @Test
    public void testStorelessStatisticWithNulls() {
        df.set(0, 2, null);
        df.set(1, 3, null);
        df.mean();
    }

    @Test
    public void testStatisticWithNulls() {
        df.set(0, 2, null);
        df.set(1, 3, null);
        df.median();
    }

    @Test
    public void testColumnSpecificAggregations() throws Exception {
        assertArrayEquals(
                new Object[]{"one", "two", "three", 30.0, 70.0, 180.0, 15.0, 35.0, 60.0},
                df.groupBy("b")
                        .bindAggregate("c", new Aggregation.Sum())
                        .bindAggregate("d", new Aggregation.Mean())
                        .applyColumnSpecificAggregations().toArray());
    }

    @Test
    public void regression() {
        Object[] expected = df
                .groupBy("b")
                .sum().toArray();

        Object[] actual = df.groupBy("b")
                .bindAggregate("c", new Aggregation.Sum())
                .bindAggregate("d", new Aggregation.Sum())
                .applyColumnSpecificAggregations().toArray();

        assertArrayEquals(
                expected,
                actual
        );
    }
}
