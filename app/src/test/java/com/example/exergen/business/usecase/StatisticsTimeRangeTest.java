package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatisticsTimeRangeTest {

    @Test
    public void fromSpinnerPosition_ReturnsExpectedValues() {
        assertEquals(StatisticsTimeRange.ALL_TIME, StatisticsTimeRange.fromSpinnerPosition(0));
        assertEquals(StatisticsTimeRange.LAST_7_DAYS, StatisticsTimeRange.fromSpinnerPosition(1));
        assertEquals(StatisticsTimeRange.LAST_30_DAYS, StatisticsTimeRange.fromSpinnerPosition(2));
    }

    @Test
    public void fromSpinnerPosition_InvalidValueFallsBackToAllTime() {
        assertEquals(StatisticsTimeRange.ALL_TIME, StatisticsTimeRange.fromSpinnerPosition(99));
    }
}
