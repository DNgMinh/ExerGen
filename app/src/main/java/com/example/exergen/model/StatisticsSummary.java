package com.example.exergen.model;

import com.example.exergen.business.service.StatisticsConstants;
import com.example.exergen.business.service.StatisticsValidation;

public class StatisticsSummary {
    private final int totalSessions;
    private final int cumulativeDurationSeconds;
    private final int averageSessionLengthSeconds;
    private final int totalEstimatedCalories;
    private final int averageEstimatedCalories;

    public StatisticsSummary(
            int totalSessions,
            int cumulativeDurationSeconds,
            int averageSessionLengthSeconds,
            int totalEstimatedCalories,
            int averageEstimatedCalories) {
        StatisticsValidation.requireNonNegative(totalSessions, StatisticsConstants.MESSAGE_TOTAL_SESSIONS_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(
                cumulativeDurationSeconds,
                StatisticsConstants.MESSAGE_CUMULATIVE_DURATION_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(
                averageSessionLengthSeconds,
                StatisticsConstants.MESSAGE_AVERAGE_DURATION_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(
                totalEstimatedCalories,
                StatisticsConstants.MESSAGE_TOTAL_CALORIES_NON_NEGATIVE);
        StatisticsValidation.requireNonNegative(
                averageEstimatedCalories,
                StatisticsConstants.MESSAGE_AVERAGE_CALORIES_NON_NEGATIVE);

        this.totalSessions = totalSessions;
        this.cumulativeDurationSeconds = cumulativeDurationSeconds;
        this.averageSessionLengthSeconds = averageSessionLengthSeconds;
        this.totalEstimatedCalories = totalEstimatedCalories;
        this.averageEstimatedCalories = averageEstimatedCalories;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getCumulativeDurationSeconds() {
        return cumulativeDurationSeconds;
    }

    public int getAverageSessionLengthSeconds() {
        return averageSessionLengthSeconds;
    }

    public int getTotalEstimatedCalories() {
        return totalEstimatedCalories;
    }

    public int getAverageEstimatedCalories() {
        return averageEstimatedCalories;
    }
}
