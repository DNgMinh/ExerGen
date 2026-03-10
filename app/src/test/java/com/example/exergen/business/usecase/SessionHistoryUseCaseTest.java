package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SessionHistoryUseCaseTest {
    private InMemorySessionHistoryRepository repository;
    private SessionHistoryUseCase useCase;

    @Before
    public void setUp() {
        repository = new InMemorySessionHistoryRepository();
        useCase = new SessionHistoryUseCase(repository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullRepository() {
        new SessionHistoryUseCase(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveCompletedSessionRejectsNullRecord() {
        useCase.saveCompletedSession(null);
    }

    @Test
    public void saveCompletedSessionPersistsRecord() {
        SessionRecord record = createRecord("s-1", 1700000000000L);

        useCase.saveCompletedSession(record);

        assertEquals(record, repository.getSessionById("s-1"));
    }

    @Test
    public void getSessionHistoryReturnsNewestFirst() {
        SessionRecord older = createRecord("older", 1700000000000L);
        SessionRecord newest = createRecord("newest", 1700000009000L);
        SessionRecord middle = createRecord("middle", 1700000004000L);

        repository.saveSession(older);
        repository.saveSession(newest);
        repository.saveSession(middle);

        List<SessionRecord> results = useCase.getSessionHistory();

        assertEquals(3, results.size());
        assertEquals("newest", results.get(0).getId());
        assertEquals("middle", results.get(1).getId());
        assertEquals("older", results.get(2).getId());
    }

    @Test
    public void getSessionHistoryReturnsEmptyListWhenRepositoryReturnsNull() {
        SessionHistoryUseCase nullListUseCase = new SessionHistoryUseCase(new NullListSessionHistoryRepository());

        List<SessionRecord> results = nullListUseCase.getSessionHistory();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    private static SessionRecord createRecord(String id, long completedAtEpochMs) {
        return new SessionRecord(
                id,
                "workout-1",
                "Workout A",
                completedAtEpochMs,
                600,
                5,
                3,
                3);
    }

    private static class InMemorySessionHistoryRepository implements ISessionHistoryRepository {
        private final List<SessionRecord> store = new ArrayList<>();

        @Override
        public void saveSession(SessionRecord sessionRecord) {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).getId().equals(sessionRecord.getId())) {
                    store.set(i, sessionRecord);
                    return;
                }
            }
            store.add(sessionRecord);
        }

        @Override
        public SessionRecord getSessionById(String sessionId) {
            for (SessionRecord record : store) {
                if (record.getId().equals(sessionId)) {
                    return record;
                }
            }
            return null;
        }

        @Override
        public List<SessionRecord> getAllSessions() {
            return new ArrayList<>(store);
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
