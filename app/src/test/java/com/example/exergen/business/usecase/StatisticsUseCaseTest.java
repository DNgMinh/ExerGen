package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.business.exception.StatisticsValidationException;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.model.StatisticsSummary;
import com.example.exergen.model.WeeklyTrendPoint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUseCaseTest {

    @Test(expected = StatisticsValidationException.class)
    public void constructorRejectsNullRepository() {
        new StatisticsUseCase(null);
    }

    @Test
    public void getOverallSummaryReturnsZerosWhenNoSessions() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());

        StatisticsSummary summary = useCase.getOverallSummary();

        assertEquals(0, summary.getTotalSessions());
        assertEquals(0, summary.getCumulativeDurationSeconds());
        assertEquals(0, summary.getAverageSessionLengthSeconds());
        assertEquals(0, summary.getTotalEstimatedCalories());
        assertEquals(0, summary.getAverageEstimatedCalories());
    }

    @Test
    public void getOverallSummaryComputesAggregateValues() {
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        // 600 sec => 10 min => 80 kcal (using 8 kcal/min)
        repository.saveSession(createRecord("s-1", 1700000000000L, 600));
        // 900 sec => 15 min => 120 kcal
        repository.saveSession(createRecord("s-2", 1700000001000L, 900));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        StatisticsSummary summary = useCase.getOverallSummary();

        assertEquals(2, summary.getTotalSessions());
        assertEquals(1500, summary.getCumulativeDurationSeconds());
        assertEquals(750, summary.getAverageSessionLengthSeconds());
        assertEquals(200, summary.getTotalEstimatedCalories());
        assertEquals(100, summary.getAverageEstimatedCalories());
    }

    @Test
    public void getOverallSummaryHandlesNullSessionListAsEmpty() {
        StatisticsUseCase useCase = new StatisticsUseCase(new NullListSessionHistoryRepository());

        StatisticsSummary summary = useCase.getOverallSummary();

        assertEquals(0, summary.getTotalSessions());
        assertEquals(0, summary.getCumulativeDurationSeconds());
        assertEquals(0, summary.getAverageSessionLengthSeconds());
        assertEquals(0, summary.getTotalEstimatedCalories());
        assertEquals(0, summary.getAverageEstimatedCalories());
    }

    @Test
    public void getSummaryForTimeRangeIncludesOnlyLastSevenDays() {
        long now = 2_000_000_000_000L;
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("in-7d", now - toMs(3), 600));
        repository.saveSession(createRecord("out-7d", now - toMs(8), 300));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        StatisticsSummary summary = useCase.getSummaryForTimeRange(StatisticsTimeRange.LAST_7_DAYS, now);

        assertEquals(1, summary.getTotalSessions());
        assertEquals(600, summary.getCumulativeDurationSeconds());
        assertEquals(80, summary.getTotalEstimatedCalories());
    }

    @Test
    public void getSummaryForTimeRangeIncludesOnlyLastThirtyDays() {
        long now = 2_000_000_000_000L;
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("in-30d", now - toMs(15), 900));
        repository.saveSession(createRecord("out-30d", now - toMs(31), 600));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        StatisticsSummary summary = useCase.getSummaryForTimeRange(StatisticsTimeRange.LAST_30_DAYS, now);

        assertEquals(1, summary.getTotalSessions());
        assertEquals(900, summary.getCumulativeDurationSeconds());
        assertEquals(120, summary.getTotalEstimatedCalories());
    }

    @Test
    public void getSummaryForTimeRangeAllTimeMatchesOverallSummary() {
        long now = 2_000_000_000_000L;
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("s-1", now - toMs(40), 600));
        repository.saveSession(createRecord("s-2", now - toMs(1), 300));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        StatisticsSummary allTimeSummary = useCase.getSummaryForTimeRange(StatisticsTimeRange.ALL_TIME, now);
        StatisticsSummary overallSummary = useCase.getOverallSummary();

        assertEquals(overallSummary.getTotalSessions(), allTimeSummary.getTotalSessions());
        assertEquals(overallSummary.getCumulativeDurationSeconds(), allTimeSummary.getCumulativeDurationSeconds());
        assertEquals(overallSummary.getAverageSessionLengthSeconds(), allTimeSummary.getAverageSessionLengthSeconds());
        assertEquals(overallSummary.getTotalEstimatedCalories(), allTimeSummary.getTotalEstimatedCalories());
        assertEquals(overallSummary.getAverageEstimatedCalories(), allTimeSummary.getAverageEstimatedCalories());
    }

    @Test(expected = InvalidFilterException.class)
    public void getSummaryForTimeRangeRejectsNullRange() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());
        useCase.getSummaryForTimeRange(null, 2_000_000_000_000L);
    }

    @Test(expected = StatisticsValidationException.class)
    public void getSummaryForTimeRangeRejectsInvalidNowValue() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());
        useCase.getSummaryForTimeRange(StatisticsTimeRange.LAST_7_DAYS, 0L);
    }

    @Test
    public void getWeeklyTrendSeriesForLastThirtyDaysBuildsExpectedBuckets() {
        long now = 2_000_000_000_000L;
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("w0-a", now - toMs(2), 600));
        repository.saveSession(createRecord("w0-b", now - toMs(5), 300));
        repository.saveSession(createRecord("w1", now - toMs(10), 900));
        repository.saveSession(createRecord("w2", now - toMs(20), 120));
        repository.saveSession(createRecord("out", now - toMs(35), 700));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        List<WeeklyTrendPoint> points = useCase.getWeeklyTrendSeries(StatisticsTimeRange.LAST_30_DAYS, now);

        assertEquals(5, points.size());

        // oldest to newest: offsets 4,3,2,1,0
        assertEquals(4, points.get(0).getWeekOffsetFromCurrent());
        assertEquals(0, points.get(0).getSessionCount());
        assertEquals(0, points.get(0).getAverageDurationSeconds());

        assertEquals(3, points.get(1).getWeekOffsetFromCurrent());
        assertEquals(0, points.get(1).getSessionCount());

        assertEquals(2, points.get(2).getWeekOffsetFromCurrent());
        assertEquals(1, points.get(2).getSessionCount());
        assertEquals(120, points.get(2).getAverageDurationSeconds());

        assertEquals(1, points.get(3).getWeekOffsetFromCurrent());
        assertEquals(1, points.get(3).getSessionCount());
        assertEquals(900, points.get(3).getAverageDurationSeconds());

        assertEquals(0, points.get(4).getWeekOffsetFromCurrent());
        assertEquals(2, points.get(4).getSessionCount());
        assertEquals(450, points.get(4).getAverageDurationSeconds());
    }

    @Test
    public void getWeeklyTrendSeriesForAllTimeIncludesOlderWeeks() {
        long now = 2_000_000_000_000L;
        InMemorySessionHistoryRepository repository = new InMemorySessionHistoryRepository();
        repository.saveSession(createRecord("recent", now - toMs(1), 600));
        repository.saveSession(createRecord("older", now - toMs(40), 300));

        StatisticsUseCase useCase = new StatisticsUseCase(repository);
        List<WeeklyTrendPoint> points = useCase.getWeeklyTrendSeries(StatisticsTimeRange.ALL_TIME, now);

        assertEquals(6, points.size());
        assertEquals(5, points.get(0).getWeekOffsetFromCurrent());
        assertEquals(1, points.get(0).getSessionCount());
        assertEquals(300, points.get(0).getAverageDurationSeconds());
        assertEquals(0, points.get(5).getWeekOffsetFromCurrent());
        assertEquals(1, points.get(5).getSessionCount());
        assertEquals(600, points.get(5).getAverageDurationSeconds());
    }

    @Test
    public void getWeeklyTrendSeriesReturnsEmptyWhenNoSessions() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());
        List<WeeklyTrendPoint> points = useCase.getWeeklyTrendSeries(StatisticsTimeRange.LAST_7_DAYS, 2_000_000_000_000L);
        assertTrue(points.isEmpty());
    }

    @Test(expected = InvalidFilterException.class)
    public void getWeeklyTrendSeriesRejectsNullRange() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());
        useCase.getWeeklyTrendSeries(null, 2_000_000_000_000L);
    }

    @Test(expected = StatisticsValidationException.class)
    public void getWeeklyTrendSeriesRejectsInvalidNowValue() {
        StatisticsUseCase useCase = new StatisticsUseCase(new InMemorySessionHistoryRepository());
        useCase.getWeeklyTrendSeries(StatisticsTimeRange.ALL_TIME, 0L);
    }

    private static long toMs(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }

    private static SessionRecord createRecord(String id, long completedAtEpochMs, int durationSeconds) {
        return new SessionRecord(
                id,
                "workout-1",
                "Workout",
                completedAtEpochMs,
                durationSeconds,
                5,
                3,
                3);
    }

    private static class InMemorySessionHistoryRepository implements ISessionHistoryRepository {
        private final List<SessionRecord> records = new ArrayList<>();

        @Override
        public void saveSession(SessionRecord sessionRecord) {
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).getId().equals(sessionRecord.getId())) {
                    records.set(i, sessionRecord);
                    return;
                }
            }
            records.add(sessionRecord);
        }

        @Override
        public SessionRecord getSessionById(String sessionId) {
            for (SessionRecord record : records) {
                if (record.getId().equals(sessionId)) {
                    return record;
                }
            }
            return null;
        }

        @Override
        public List<SessionRecord> getAllSessions() {
            return new ArrayList<>(records);
        }
    }

    private static class NullListSessionHistoryRepository implements ISessionHistoryRepository {
        @Override
        public void saveSession(SessionRecord sessionRecord) {
        }

        @Override
        public SessionRecord getSessionById(String sessionId) {
            return null;
        }

        @Override
        public List<SessionRecord> getAllSessions() {
            return null;
        }
    }
}
