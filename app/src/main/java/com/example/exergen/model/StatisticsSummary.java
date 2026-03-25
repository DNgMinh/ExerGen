package com.example.exergen.model;

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
        ModelValidation.requireNonNegative(totalSessions, "Total sessions must be >= 0");
        ModelValidation.requireNonNegative(cumulativeDurationSeconds, "Cumulative duration must be >= 0");
        ModelValidation.requireNonNegative(averageSessionLengthSeconds, "Average duration must be >= 0");
        ModelValidation.requireNonNegative(totalEstimatedCalories, "Total calories must be >= 0");
        ModelValidation.requireNonNegative(averageEstimatedCalories, "Average calories must be >= 0");

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
