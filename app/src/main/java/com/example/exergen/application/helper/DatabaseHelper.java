package com.example.exergen.application.helper;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

public class DatabaseHelper extends SupportSQLiteOpenHelper.Callback {

    public static final String DEFAULT_DATABASE_NAME = "ExerGen.db";
    private static final int DATABASE_VERSION = 7;

    public static final String TABLE_EXERCISE = "Exercise";
    public static final String TABLE_WORKOUT = "Workout";
    public static final String TABLE_SESSION_HISTORY = "SessionHistory";

    private final SupportSQLiteOpenHelper openHelper;

    public DatabaseHelper(Context context) {
        this(context, DEFAULT_DATABASE_NAME);
    }

    public DatabaseHelper(Context context, String databaseName) {
        super(DATABASE_VERSION);
        SupportSQLiteOpenHelper.Configuration config = SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(databaseName)
                .callback(this)
                .build();
        this.openHelper = new FrameworkSQLiteOpenHelperFactory().create(config);
    }

    public SupportSQLiteDatabase getWritableDatabase() {
        return openHelper.getWritableDatabase();
    }

    public SupportSQLiteDatabase getReadableDatabase() {
        return openHelper.getReadableDatabase();
    }

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EXERCISE + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "muscle_groups TEXT, " +
                "equipment TEXT, " +
                "instructions TEXT, " +
                "intensity INTEGER, " +
                "image_paths TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_WORKOUT + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "sets INTEGER, " +
                "exercise_ids TEXT, " +
                "work_seconds TEXT, " +
                "rest_seconds TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SESSION_HISTORY + " (" +
                "id TEXT PRIMARY KEY, " +
                "workout_id TEXT NOT NULL, " +
                "workout_name TEXT NOT NULL, " +
                "completed_at_epoch_ms INTEGER NOT NULL, " +
                "total_duration_seconds INTEGER NOT NULL, " +
                "exercise_count INTEGER NOT NULL, " +
                "sets_planned INTEGER NOT NULL, " +
                "sets_completed INTEGER NOT NULL, " +
                "estimated_calories INTEGER)");
    }

    @Override
    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
