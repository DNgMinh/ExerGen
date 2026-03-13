package com.example.exergen.business.service;

public final class StatisticsConstants {
    private StatisticsConstants() {
    }

    public static final double ESTIMATED_CALORIES_PER_MINUTE = 8.0;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final long MS_PER_DAY = 24L * 60L * 60L * 1000L;
    public static final long MS_PER_WEEK = 7L * MS_PER_DAY;
    public static final int LAST_SEVEN_DAYS_WEEK_OFFSET = 0;
    public static final int LAST_THIRTY_DAYS_MAX_WEEK_OFFSET = 4;

    public static final String MESSAGE_SESSION_HISTORY_REPOSITORY_REQUIRED = "sessionHistoryRepository required";
    public static final String MESSAGE_TIME_RANGE_REQUIRED = "Time range required.";
    public static final String MESSAGE_NOW_EPOCH_MS_POSITIVE = "nowEpochMs must be > 0";
    public static final String MESSAGE_TOTAL_SESSIONS_NON_NEGATIVE = "totalSessions must be >= 0";
    public static final String MESSAGE_CUMULATIVE_DURATION_NON_NEGATIVE = "cumulativeDurationSeconds must be >= 0";
    public static final String MESSAGE_AVERAGE_DURATION_NON_NEGATIVE = "averageSessionLengthSeconds must be >= 0";
    public static final String MESSAGE_TOTAL_CALORIES_NON_NEGATIVE = "totalEstimatedCalories must be >= 0";
    public static final String MESSAGE_AVERAGE_CALORIES_NON_NEGATIVE = "averageEstimatedCalories must be >= 0";
    public static final String MESSAGE_WEEK_OFFSET_NON_NEGATIVE = "weekOffsetFromCurrent must be >= 0";
    public static final String MESSAGE_SESSION_COUNT_NON_NEGATIVE = "sessionCount must be >= 0";
    public static final String MESSAGE_AVERAGE_SECONDS_NON_NEGATIVE = "averageDurationSeconds must be >= 0";
}
