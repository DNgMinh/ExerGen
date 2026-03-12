package com.example.exergen.business.usecase;

import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.business.service.StatisticsAggregationService;
import com.example.exergen.business.service.StatisticsConstants;
import com.example.exergen.business.service.StatisticsValidation;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUseCase {
    private final ISessionHistoryRepository sessionHistoryRepository;
    private final StatisticsAggregationService aggregationService;

    public StatisticsUseCase(ISessionHistoryRepository sessionHistoryRepository) {
        StatisticsValidation.requireNonNull(
                sessionHistoryRepository,
                StatisticsConstants.MESSAGE_SESSION_HISTORY_REPOSITORY_REQUIRED);
        this.sessionHistoryRepository = sessionHistoryRepository;
        this.aggregationService = new StatisticsAggregationService();
    }

    public StatisticsSummary getOverallSummary() {
        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        return aggregationService.buildSummary(sessions);
    }

    public StatisticsSummary getSummaryForTimeRange(StatisticsTimeRange timeRange) {
        return getSummaryForTimeRange(timeRange, System.currentTimeMillis());
    }

    StatisticsSummary getSummaryForTimeRange(StatisticsTimeRange timeRange, long nowEpochMs) {
        if (timeRange == null) {
            throw new InvalidFilterException(StatisticsConstants.MESSAGE_TIME_RANGE_REQUIRED);
        }
        StatisticsValidation.requirePositive(nowEpochMs, StatisticsConstants.MESSAGE_NOW_EPOCH_MS_POSITIVE);

        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        if (sessions == null || sessions.isEmpty() || timeRange == StatisticsTimeRange.ALL_TIME) {
            return aggregationService.buildSummary(sessions);
        }

        long lowerBound = nowEpochMs - (timeRange.getDays() * StatisticsConstants.MS_PER_DAY);
        List<SessionRecord> filteredSessions = new ArrayList<>();
        for (SessionRecord session : sessions) {
            long completedAt = session.getCompletedAtEpochMs();
            if (completedAt >= lowerBound && completedAt <= nowEpochMs) {
                filteredSessions.add(session);
            }
        }

        return aggregationService.buildSummary(filteredSessions);
    }

    public List<WeeklyTrendPoint> getWeeklyTrendSeries(StatisticsTimeRange timeRange) {
        return getWeeklyTrendSeries(timeRange, System.currentTimeMillis());
    }

    List<WeeklyTrendPoint> getWeeklyTrendSeries(StatisticsTimeRange timeRange, long nowEpochMs) {
        if (timeRange == null) {
            throw new InvalidFilterException(StatisticsConstants.MESSAGE_TIME_RANGE_REQUIRED);
        }
        StatisticsValidation.requirePositive(nowEpochMs, StatisticsConstants.MESSAGE_NOW_EPOCH_MS_POSITIVE);

        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        if (sessions == null || sessions.isEmpty()) {
            return new ArrayList<>();
        }

        List<SessionRecord> includedSessions = new ArrayList<>();
        long lowerBound = 0L;
        if (timeRange != StatisticsTimeRange.ALL_TIME) {
            lowerBound = nowEpochMs - (timeRange.getDays() * StatisticsConstants.MS_PER_DAY);
        }

        int maxWeekOffset = -1;
        for (SessionRecord session : sessions) {
            long completedAt = session.getCompletedAtEpochMs();
            if (completedAt > nowEpochMs) {
                continue;
            }
            if (timeRange != StatisticsTimeRange.ALL_TIME && completedAt < lowerBound) {
                continue;
            }

            includedSessions.add(session);
            int weekOffset = (int) ((nowEpochMs - completedAt) / StatisticsConstants.MS_PER_WEEK);
            if (weekOffset > maxWeekOffset) {
                maxWeekOffset = weekOffset;
            }
        }

        if (includedSessions.isEmpty()) {
            return new ArrayList<>();
        }

        if (timeRange == StatisticsTimeRange.LAST_7_DAYS) {
            maxWeekOffset = StatisticsConstants.LAST_SEVEN_DAYS_WEEK_OFFSET;
        } else if (timeRange == StatisticsTimeRange.LAST_30_DAYS) {
            maxWeekOffset = StatisticsConstants.LAST_THIRTY_DAYS_MAX_WEEK_OFFSET;
        }
        return aggregationService.buildTrendSeries(includedSessions, maxWeekOffset, nowEpochMs);
    }
}
