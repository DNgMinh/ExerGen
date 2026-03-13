package com.example.exergen.application.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.business.repository.ISessionHistoryRepository;

import java.util.ArrayList;
import java.util.List;

public class SessionHistoryRepositorySQLite implements ISessionHistoryRepository {
    private final DatabaseHelper dbHelper;

    public SessionHistoryRepositorySQLite(Context context) {
        this(context, DatabaseHelper.DEFAULT_DATABASE_NAME);
    }

    public SessionHistoryRepositorySQLite(Context context, String databaseName) {
        this.dbHelper = new DatabaseHelper(context, databaseName);
    }

    @Override
    public void saveSession(SessionRecord record) {
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", record.getId());
        values.put("workout_id", record.getWorkoutId());
        values.put("workout_name", record.getWorkoutName());
        values.put("completed_at_epoch_ms", record.getCompletedAtEpochMs());
        values.put("total_duration_seconds", record.getTotalDurationSeconds());
        values.put("exercise_count", record.getExerciseCount());
        values.put("rounds_planned", record.getRoundsPlanned());
        values.put("rounds_completed", record.getRoundsCompleted());

        db.insert(DatabaseHelper.TABLE_SESSION_HISTORY, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE, values);
    }

    @Override
    public List<SessionRecord> getAllSessions() {
        List<SessionRecord> sessions = new ArrayList<>();
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query("SELECT * FROM " + DatabaseHelper.TABLE_SESSION_HISTORY + 
                                      " ORDER BY completed_at_epoch_ms DESC")) {
            if (cursor.moveToFirst()) {
                do {
                    sessions.add(mapCursorToSessionRecord(cursor));
                } while (cursor.moveToNext());
            }
        }
        return sessions;
    }

    @Override
    public SessionRecord getSessionById(String id) {
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        SessionRecord record = null;

        try (Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(DatabaseHelper.TABLE_SESSION_HISTORY)
                .selection("id = ?", new Object[]{id})
                .create())) {
            if (cursor.moveToFirst()) {
                record = mapCursorToSessionRecord(cursor);
            }
        }
        return record;
    }

    private SessionRecord mapCursorToSessionRecord(Cursor cursor) {
        return new SessionRecord(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("workout_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("workout_name")),
                cursor.getLong(cursor.getColumnIndexOrThrow("completed_at_epoch_ms")),
                cursor.getInt(cursor.getColumnIndexOrThrow("total_duration_seconds")),
                cursor.getInt(cursor.getColumnIndexOrThrow("rounds_planned")),
                cursor.getInt(cursor.getColumnIndexOrThrow("exercise_count")),
                cursor.getInt(cursor.getColumnIndexOrThrow("rounds_completed"))
        );
    }
}
