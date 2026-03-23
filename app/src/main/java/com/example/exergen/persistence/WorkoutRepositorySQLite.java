package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.Workout;
import com.example.exergen.business.repository.IWorkoutRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutRepositorySQLite implements IWorkoutRepository {
    private final DatabaseHelper dbHelper;

    public WorkoutRepositorySQLite(Context context) {
        this(context, DatabaseHelper.DEFAULT_DATABASE_NAME);
    }

    public WorkoutRepositorySQLite(Context context, String databaseName) {
        this.dbHelper = new DatabaseHelper(context, databaseName);
    }

    @Override
    public void saveWorkout(Workout workout) {
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String exerciseIdsStr = String.join(",", workout.getExerciseIds());

        StringBuilder workSecStr = new StringBuilder();
        for (int i = 0; i < workout.getWorkSeconds().size(); i++) {
            workSecStr.append(workout.getWorkSeconds().get(i));
            if (i < workout.getWorkSeconds().size() - 1)
                workSecStr.append(",");
        }

        StringBuilder restSecStr = new StringBuilder();
        for (int i = 0; i < workout.getRestSeconds().size(); i++) {
            restSecStr.append(workout.getRestSeconds().get(i));
            if (i < workout.getRestSeconds().size() - 1)
                restSecStr.append(",");
        }

        values.put("id", workout.getId());
        values.put("name", workout.getName());
        values.put("sets", workout.getSets());
        values.put("exercise_ids", exerciseIdsStr);
        values.put("work_seconds", workSecStr.toString());
        values.put("rest_seconds", restSecStr.toString());

        db.insert(DatabaseHelper.TABLE_WORKOUT, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE, values);
    }

    @Override
    public Workout getWorkoutById(String workoutId) {
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        Workout workout = null;

        try (Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(DatabaseHelper.TABLE_WORKOUT)
                .selection("id = ?", new Object[]{workoutId})
                .create())) {

            if (cursor.moveToFirst()) {
                workout = parseCursorToWorkout(cursor);
            }
        }
        return workout;
    }

    @Override
    public List<Workout> getAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query("SELECT * FROM " + DatabaseHelper.TABLE_WORKOUT)) {
            if (cursor.moveToFirst()) {
                do {
                    workouts.add(parseCursorToWorkout(cursor));
                } while (cursor.moveToNext());
            }
        }
        return workouts;
    }

    @Override
    public void deleteWorkout(String workoutId) {
        if (workoutId == null || workoutId.trim().isEmpty()) {
            return;
        }
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_WORKOUT, "id = ?", new Object[]{workoutId});
    }

    private Workout parseCursorToWorkout(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));
        String exIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("exercise_ids"));
        String workSecStr = cursor.getString(cursor.getColumnIndexOrThrow("work_seconds"));
        String restSecStr = cursor.getString(cursor.getColumnIndexOrThrow("rest_seconds"));

        List<String> exerciseIds = Arrays.asList(exIdsStr.split(","));

        List<Integer> workSeconds = new ArrayList<>();
        for (String s : workSecStr.split(",")) {
            if (!s.trim().isEmpty())
                workSeconds.add(Integer.parseInt(s.trim()));
        }

        List<Integer> restSeconds = new ArrayList<>();
        for (String s : restSecStr.split(",")) {
            if (!s.trim().isEmpty())
                restSeconds.add(Integer.parseInt(s.trim()));
        }

        return new Workout(id, name, sets, exerciseIds, workSeconds, restSeconds);
    }

    @Override
    public void seedData() {
        if (getAllWorkouts().isEmpty()) {
            List<Workout> defaultWorkouts = DefaultWorkoutSeedData.createDefaultWorkouts();
            for (Workout w : defaultWorkouts) {
                saveWorkout(w);
            }
        }
    }
}
