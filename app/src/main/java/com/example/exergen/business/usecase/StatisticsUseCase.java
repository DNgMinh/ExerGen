package com.example.exergen.business.usecase;

import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUseCase {
    // A lightweight fixed estimate to provide consistent baseline metrics.
    private static final double ESTIMATED_CALORIES_PER_MINUTE = 8.0;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final long MS_PER_DAY = 24L * 60L * 60L * 1000L;

    private final ISessionHistoryRepository sessionHistoryRepository;

    public StatisticsUseCase(ISessionHistoryRepository sessionHistoryRepository) {
        if (sessionHistoryRepository == null) {
            throw new IllegalArgumentException("sessionHistoryRepository required");
        }
        this.sessionHistoryRepository = sessionHistoryRepository;
    }

    public StatisticsSummary getOverallSummary() {
        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        return buildSummary(sessions);
    }

    public StatisticsSummary getSummaryForTimeRange(StatisticsTimeRange timeRange) {
        return getSummaryForTimeRange(timeRange, System.currentTimeMillis());
    }

    StatisticsSummary getSummaryForTimeRange(StatisticsTimeRange timeRange, long nowEpochMs) {
        if (timeRange == null) {
            throw new InvalidFilterException("Time range required.");
        }
        if (nowEpochMs <= 0L) {
            throw new IllegalArgumentException("nowEpochMs must be > 0");
        }

        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        if (sessions == null || sessions.isEmpty() || timeRange == StatisticsTimeRange.ALL_TIME) {
            return buildSummary(sessions);
        }

        long lowerBound = nowEpochMs - (timeRange.getDays() * MS_PER_DAY);
        List<SessionRecord> filteredSessions = new ArrayList<>();
        for (SessionRecord session : sessions) {
            long completedAt = session.getCompletedAtEpochMs();
            if (completedAt >= lowerBound && completedAt <= nowEpochMs) {
                filteredSessions.add(session);
            }
        }

        return buildSummary(filteredSessions);
    }

    private StatisticsSummary buildSummary(List<SessionRecord> sessions) {
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
