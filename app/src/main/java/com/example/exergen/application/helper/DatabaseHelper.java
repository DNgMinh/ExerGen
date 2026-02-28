package com.example.exergen.application.helper;

import android.content.Context;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

public class DatabaseHelper extends SupportSQLiteOpenHelper.Callback {

    private static final int DATABASE_VERSION = 1;

    // --- EXERCISE TABLE ---
    public static final String TABLE_EXERCISE = "Exercise";
    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + TABLE_EXERCISE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "muscle_group TEXT NOT NULL, " +
                    "equipment TEXT NOT NULL, " +
                    "intensity_per_minute REAL);";

    // --- WORKOUT TABLE ---
    public static final String TABLE_WORKOUT = "Workout";
    private static final String CREATE_WORKOUT_TABLE =
            "CREATE TABLE " + TABLE_WORKOUT + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "total_duration_minutes INTEGER, " +
                    "work_interval_seconds INTEGER, " +
                    "rest_interval_seconds INTEGER, " +
                    "created_at INTEGER);"; // Unix timestamp

    // --- WORKOUT EXERCISE TABLE (Maps workouts to exercises) ---
    public static final String TABLE_WORKOUT_EXERCISE = "Workout_Exercise";
    private static final String CREATE_WORKOUT_EXERCISE_TABLE =
            "CREATE TABLE " + TABLE_WORKOUT_EXERCISE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "workout_id INTEGER, " +
                    "exercise_id INTEGER, " +
                    "sequence_order INTEGER, " +
                    "FOREIGN KEY(workout_id) REFERENCES " + TABLE_WORKOUT + "(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(exercise_id) REFERENCES " + TABLE_EXERCISE + "(id) ON DELETE CASCADE);";

    public DatabaseHelper() {
        super(DATABASE_VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        // This runs the very first time the app requests the database
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_WORKOUT_TABLE);
        db.execSQL(CREATE_WORKOUT_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        // Used later if you change your schema (e.g., in Iteration 3)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);
    }
}