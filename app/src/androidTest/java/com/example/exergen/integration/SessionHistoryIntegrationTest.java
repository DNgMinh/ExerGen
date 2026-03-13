package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.business.usecase.SessionHistoryUseCase;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.application.persistence.SessionHistoryRepositorySQLite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SessionHistoryIntegrationTest {
    private static final String TEST_DB_NAME = "ExerGen_test.db";

    private SessionHistoryUseCase sessionHistoryUseCase;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(TEST_DB_NAME);

        SessionHistoryRepositorySQLite repository = new SessionHistoryRepositorySQLite(context, TEST_DB_NAME);
        sessionHistoryUseCase = new SessionHistoryUseCase(repository);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(TEST_DB_NAME);
    }

    @Test
    public void testSaveAndRetrieveSession_PersistsToRealSQLite() {
        String sessionId = "it-session-save";
        SessionRecord record = new SessionRecord(
                sessionId,
                "workout-123",
                "Integration Workout",
                1700000000000L,
                1200,
                8,
                4,
                4
        );

        sessionHistoryUseCase.saveCompletedSession(record);
        
        SessionRecord retrieved = sessionHistoryUseCase.getSessionById(sessionId);
        assertNotNull(retrieved);
        assertEquals("Integration Workout", retrieved.getWorkoutName());
        assertEquals(1200, retrieved.getTotalDurationSeconds());
    }

    @Test
    public void testGetSessionHistory_ReturnsOrderedFromRealSQLite() {
        SessionRecord older = createRecord("s1", 1700000000000L);
        SessionRecord newer = createRecord("s2", 1700000009000L);

        sessionHistoryUseCase.saveCompletedSession(older);
        sessionHistoryUseCase.saveCompletedSession(newer);

        List<SessionRecord> history = sessionHistoryUseCase.getSessionHistory();
        
        assertEquals(2, history.size());
        assertEquals("s2", history.get(0).getId()); // Order by newest first
        assertEquals("s1", history.get(1).getId());
    }

    private SessionRecord createRecord(String id, long completedAtMs) {
        return new SessionRecord(
                id, "w1", "Workout", completedAtMs, 600, 5, 3, 3);
    }
}
