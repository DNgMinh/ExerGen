package com.example.exergen.business.service;

import com.example.exergen.business.usecase.StatisticsSummary;
import com.example.exergen.business.usecase.WeeklyTrendPoint;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.List;

public final class StatisticsAggregationService {
    public StatisticsSummary buildSummary(List<SessionRecord> sessions) {
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

    public List<WeeklyTrendPoint> buildTrendSeries(
            List<SessionRecord> includedSessions,
            int maxWeekOffset,
            long nowEpochMs) {
        if (includedSessions == null || includedSessions.isEmpty()) {
            return new ArrayList<>();
        }

        int bucketCount = maxWeekOffset + 1;
        int[] sessionCounts = new int[bucketCount];
        int[] totalDurationSeconds = new int[bucketCount];

        for (SessionRecord session : includedSessions) {
            int weekOffset = (int) ((nowEpochMs - session.getCompletedAtEpochMs()) / StatisticsConstants.MS_PER_WEEK);
            if (weekOffset >= 0 && weekOffset < bucketCount) {
                sessionCounts[weekOffset]++;
                totalDurationSeconds[weekOffset] += session.getTotalDurationSeconds();
            }
        }

        List<WeeklyTrendPoint> points = new ArrayList<>();
        for (int weekOffset = maxWeekOffset; weekOffset >= 0; weekOffset--) {
            int count = sessionCounts[weekOffset];
            int averageDurationSeconds = count == 0 ? 0 : totalDurationSeconds[weekOffset] / count;
            points.add(new WeeklyTrendPoint(weekOffset, count, averageDurationSeconds));
        }
        return points;
    }

    private int estimateCalories(int durationSeconds) {
        double durationMinutes = (double) durationSeconds / StatisticsConstants.SECONDS_PER_MINUTE;
        return (int) Math.round(durationMinutes * StatisticsConstants.ESTIMATED_CALORIES_PER_MINUTE);
    }
}
