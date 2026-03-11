package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUseCaseTest {

    @Test(expected = IllegalArgumentException.class)
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
