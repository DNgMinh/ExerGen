package com.example.exergen.application.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExerGen.db";
    // FYI: We can change the DB version during testing if we add/remove from exercises.csv.
    // Just increment version # and restart emulator
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_EXERCISE = "Exercise";
    public static final String TABLE_WORKOUT = "Workout";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    Yes, I know that technically these tables are in first normal form (Sorry Adam Pazdor!),
    but I decided on simply keeping the list of exercise_ids as a list for simplicity. Also,
    the id "should" be of INTEGER type, but using UUIDs solved the problem of closing the app
    and losing track of what the last ID was.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
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
                "rounds INTEGER, " +
                "exercise_ids TEXT, " +
                "work_seconds TEXT, " +
                "rest_seconds TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}