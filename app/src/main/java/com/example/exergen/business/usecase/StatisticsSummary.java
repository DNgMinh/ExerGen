package com.example.exergen.business.usecase;

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
        if (totalSessions < 0) {
            throw new IllegalArgumentException("totalSessions must be >= 0");
        }
        if (cumulativeDurationSeconds < 0) {
            throw new IllegalArgumentException("cumulativeDurationSeconds must be >= 0");
        }
        if (averageSessionLengthSeconds < 0) {
            throw new IllegalArgumentException("averageSessionLengthSeconds must be >= 0");
        }
        if (totalEstimatedCalories < 0) {
            throw new IllegalArgumentException("totalEstimatedCalories must be >= 0");
        }
        if (averageEstimatedCalories < 0) {
            throw new IllegalArgumentException("averageEstimatedCalories must be >= 0");
        }

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
