package com.example.exergen.model;

public class WeeklyTrendPoint {
    private final int weekOffsetFromCurrent;
    private final int sessionCount;
    private final int averageDurationSeconds;

    public WeeklyTrendPoint(int weekOffsetFromCurrent, int sessionCount, int averageDurationSeconds) {
        ModelValidation.requireNonNegative(weekOffsetFromCurrent, "Week offset must be >= 0");
        ModelValidation.requireNonNegative(sessionCount, "Session count must be >= 0");
        ModelValidation.requireNonNegative(averageDurationSeconds, "Average seconds must be >= 0");
        this.weekOffsetFromCurrent = weekOffsetFromCurrent;
        this.sessionCount = sessionCount;
        this.averageDurationSeconds = averageDurationSeconds;
    }

    public int getWeekOffsetFromCurrent() {
        return weekOffsetFromCurrent;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public int getAverageDurationSeconds() {
        return averageDurationSeconds;
    }
}
