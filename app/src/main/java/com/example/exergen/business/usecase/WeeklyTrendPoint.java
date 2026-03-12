package com.example.exergen.business.usecase;

public class WeeklyTrendPoint {
    private final int weekOffsetFromCurrent;
    private final int sessionCount;
    private final int averageDurationSeconds;

    public WeeklyTrendPoint(int weekOffsetFromCurrent, int sessionCount, int averageDurationSeconds) {
        if (weekOffsetFromCurrent < 0) {
            throw new IllegalArgumentException("weekOffsetFromCurrent must be >= 0");
        }
        if (sessionCount < 0) {
            throw new IllegalArgumentException("sessionCount must be >= 0");
        }
        if (averageDurationSeconds < 0) {
            throw new IllegalArgumentException("averageDurationSeconds must be >= 0");
        }
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
