package com.example.exergen.business.usecase;

public enum StatisticsTimeRange {
    LAST_7_DAYS(7),
    LAST_30_DAYS(30),
    ALL_TIME(0);

    private final int days;

    StatisticsTimeRange(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
