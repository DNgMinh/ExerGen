package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.List;

public class SessionHistoryRepositorySQLite implements ISessionHistoryRepository {
    private final DatabaseHelper dbHelper;

    public SessionHistoryRepositorySQLite(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public void saveSession(SessionRecord sessionRecord) {
        if (sessionRecord == null) {
            throw new IllegalArgumentException("sessionRecord required");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", sessionRecord.getId());
        values.put("workout_id", sessionRecord.getWorkoutId());
        values.put("workout_name", sessionRecord.getWorkoutName());
        values.put("completed_at_epoch_ms", sessionRecord.getCompletedAtEpochMs());
        values.put("total_duration_seconds", sessionRecord.getTotalDurationSeconds());
        values.put("exercise_count", sessionRecord.getExerciseCount());
        values.put("rounds_planned", sessionRecord.getRoundsPlanned());
        values.put("rounds_completed", sessionRecord.getRoundsCompleted());

        db.replace(DatabaseHelper.TABLE_SESSION_HISTORY, null, values);
    }

    @Override
    public SessionRecord getSessionById(String sessionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SessionRecord sessionRecord = null;

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_SESSION_HISTORY,
                null,
                "id = ?",
                new String[] { sessionId },
                null,
                null,
                null)) {
            if (cursor.moveToFirst()) {
                sessionRecord = parseCursor(cursor);
            }
        }

        return sessionRecord;
    }

    @Override
    public List<SessionRecord> getAllSessions() {
        List<SessionRecord> sessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_SESSION_HISTORY,
                null,
                null,
                null,
                null,
                null,
                "completed_at_epoch_ms DESC")) {
            if (cursor.moveToFirst()) {
                do {
                    sessions.add(parseCursor(cursor));
                } while (cursor.moveToNext());
            }
        }

        return sessions;
    }

    private SessionRecord parseCursor(Cursor cursor) {
        return new SessionRecord(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("workout_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("workout_name")),
                cursor.getLong(cursor.getColumnIndexOrThrow("completed_at_epoch_ms")),
                cursor.getInt(cursor.getColumnIndexOrThrow("total_duration_seconds")),
                cursor.getInt(cursor.getColumnIndexOrThrow("exercise_count")),
                cursor.getInt(cursor.getColumnIndexOrThrow("rounds_planned")),
                cursor.getInt(cursor.getColumnIndexOrThrow("rounds_completed")));
    }
}
