package com.example.exergen.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.SessionRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, manifest = Config.NONE)
public class SessionHistoryRepositorySQLiteTest {

    private SessionHistoryRepositorySQLite repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        repository = new SessionHistoryRepositorySQLite(context);

        DatabaseHelper helper = new DatabaseHelper(context);
        helper.getWritableDatabase().delete(DatabaseHelper.TABLE_SESSION_HISTORY, null, null);
    }

    @Test
    public void saveAndGetSessionByIdWorks() {
        SessionRecord sessionRecord = new SessionRecord(
                "s1", "w1", "Workout A", 1700000000001L, 900, 5, 3, 3);

        repository.saveSession(sessionRecord);
        SessionRecord retrieved = repository.getSessionById("s1");

        assertNotNull(retrieved);
        assertEquals("Workout A", retrieved.getWorkoutName());
        assertEquals(900, retrieved.getTotalDurationSeconds());
    }

    @Test
    public void getSessionByIdReturnsNullWhenMissing() {
        assertNull(repository.getSessionById("missing"));
    }

    @Test
    public void getAllSessionsReturnsNewestFirst() {
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
}
