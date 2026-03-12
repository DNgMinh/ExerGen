package com.example.exergen.persistence;

import com.example.exergen.model.SessionRecord;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SessionHistoryRepositoryStubTest {

    @Test
    public void saveAndGetSessionByIdWorks() {
        SessionHistoryRepositoryStub repository = new SessionHistoryRepositoryStub();
        SessionRecord sessionRecord = new SessionRecord(
                "s1", "w1", "Workout A", 1700000000001L, 900, 5, 3, 3);

        repository.saveSession(sessionRecord);
        SessionRecord retrieved = repository.getSessionById("s1");

        assertNotNull(retrieved);
        assertEquals("w1", retrieved.getWorkoutId());
    }

    @Test
    public void getSessionByIdReturnsNullWhenMissing() {
        SessionHistoryRepositoryStub repository = new SessionHistoryRepositoryStub();
        assertNull(repository.getSessionById("missing"));
    }

    @Test
    public void getAllSessionsReturnsNewestFirst() {
        SessionHistoryRepositoryStub repository = new SessionHistoryRepositoryStub();
        SessionRecord older = new SessionRecord(
                "s1", "w1", "Workout A", 1700000000000L, 900, 5, 3, 3);
        SessionRecord newer = new SessionRecord(
                "s2", "w2", "Workout B", 1700000000010L, 800, 4, 2, 2);

        repository.saveSession(older);
        repository.saveSession(newer);

        List<SessionRecord> sessions = repository.getAllSessions();
        assertEquals(2, sessions.size());
        assertEquals("s2", sessions.get(0).getId());
        assertEquals("s1", sessions.get(1).getId());
    }

    @Test
    public void saveSessionWithExistingIdReplacesRecord() {
        SessionHistoryRepositoryStub repository = new SessionHistoryRepositoryStub();
        SessionRecord original = new SessionRecord(
                "same-id", "w1", "Workout A", 1700000000000L, 900, 5, 3, 2);
        SessionRecord updated = new SessionRecord(
                "same-id", "w1", "Workout A", 1700000000100L, 950, 5, 3, 3);

        repository.saveSession(original);
        repository.saveSession(updated);

        SessionRecord retrieved = repository.getSessionById("same-id");
        assertNotNull(retrieved);
        assertEquals(950, retrieved.getTotalDurationSeconds());
        assertEquals(3, retrieved.getRoundsCompleted());
    }
}
