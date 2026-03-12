package com.example.exergen.business.usecase;

import com.example.exergen.business.service.StatisticsConstants;
import com.example.exergen.business.service.StatisticsValidation;

public class WeeklyTrendPoint {
    private final int weekOffsetFromCurrent;
    private final int sessionCount;
    private final int averageDurationSeconds;

    public WeeklyTrendPoint(int weekOffsetFromCurrent, int sessionCount, int averageDurationSeconds) {
        StatisticsValidation.requireNonNegative(
                weekOffsetFromCurrent,
                StatisticsConstants.MESSAGE_WEEK_OFFSET_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(sessionCount, StatisticsConstants.MESSAGE_SESSION_COUNT_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(
                averageDurationSeconds,
                StatisticsConstants.MESSAGE_AVERAGE_SECONDS_NON_NEGATIVE);
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
