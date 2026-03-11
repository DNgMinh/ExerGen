package com.example.exergen.business.usecase;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import java.util.List;

public class StatisticsUseCase {
    // A lightweight fixed estimate to provide consistent baseline metrics.
    private static final double ESTIMATED_CALORIES_PER_MINUTE = 8.0;
    private static final int SECONDS_PER_MINUTE = 60;

    private final ISessionHistoryRepository sessionHistoryRepository;

    public StatisticsUseCase(ISessionHistoryRepository sessionHistoryRepository) {
        if (sessionHistoryRepository == null) {
            throw new IllegalArgumentException("sessionHistoryRepository required");
        }
        this.sessionHistoryRepository = sessionHistoryRepository;
    }

    public StatisticsSummary getOverallSummary() {
        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        if (sessions == null || sessions.isEmpty()) {
            return new StatisticsSummary(0, 0, 0, 0, 0);
        }

        int totalSessions = sessions.size();
        int cumulativeDurationSeconds = 0;
        int totalEstimatedCalories = 0;

        for (SessionRecord session : sessions) {
            int durationSeconds = session.getTotalDurationSeconds();
            cumulativeDurationSeconds += durationSeconds;
            totalEstimatedCalories += estimateCalories(durationSeconds);
        }

        int averageSessionLengthSeconds = cumulativeDurationSeconds / totalSessions;
        int averageEstimatedCalories = totalEstimatedCalories / totalSessions;

        return new StatisticsSummary(
                totalSessions,
                cumulativeDurationSeconds,
                averageSessionLengthSeconds,
                totalEstimatedCalories,
                averageEstimatedCalories);
    }

    private int estimateCalories(int durationSeconds) {
        double durationMinutes = (double) durationSeconds / SECONDS_PER_MINUTE;
        return (int) Math.round(durationMinutes * ESTIMATED_CALORIES_PER_MINUTE);
    }
}
