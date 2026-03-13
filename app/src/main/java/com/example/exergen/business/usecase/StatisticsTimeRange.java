package com.example.exergen.business.usecase;

public enum StatisticsTimeRange {
    ALL_TIME(0, 0),
    LAST_7_DAYS(7, 1),
    LAST_30_DAYS(30, 2);

    private final int days;
    private final int spinnerPosition;

    StatisticsTimeRange(int days, int spinnerPosition) {
        this.days = days;
        this.spinnerPosition = spinnerPosition;
    }

    public int getDays() {
        return days;
    }

    public int getSpinnerPosition() {
        return spinnerPosition;
    }

    public static StatisticsTimeRange fromSpinnerPosition(int spinnerPosition) {
        for (StatisticsTimeRange value : values()) {
            if (value.spinnerPosition == spinnerPosition) {
                return value;
            }
        }
        return ALL_TIME;
    }
}
