package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.example.exergen.persistence.helper.DatabaseHelper;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;
import com.example.exergen.persistence.repository.IWorkoutRepository;

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
        saveWorkoutInternal(db, workout);
    }

    private void saveWorkoutInternal(SupportSQLiteDatabase db, Workout workout) {
        ContentValues values = new ContentValues();

        StringBuilder exerciseIdsStr = new StringBuilder();
        StringBuilder workSecStr = new StringBuilder();
        StringBuilder restSecStr = new StringBuilder();
        for (int i = 0; i < workout.getSteps().size(); i++) {
            WorkoutStep step = workout.getSteps().get(i);
            exerciseIdsStr.append(step.getExerciseId());
            workSecStr.append(step.getWorkSeconds());
            restSecStr.append(step.getRestSeconds());
            if (i < workout.getSteps().size() - 1) {
                exerciseIdsStr.append(",");
                workSecStr.append(",");
                restSecStr.append(",");
            }
        }

        values.put("id", workout.getId());
        values.put("name", workout.getName());
        values.put("sets", workout.getSets());
        values.put("exercise_ids", exerciseIdsStr.toString());
        values.put("work_seconds", workSecStr.toString());
        values.put("rest_seconds", restSecStr.toString());
        values.put("created_at_ms", workout.getCreatedAtMs());

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
        try (Cursor cursor = db.query("SELECT * FROM " + DatabaseHelper.TABLE_WORKOUT + " ORDER BY created_at_ms ASC")) {
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
        long createdAtMs = cursor.getLong(cursor.getColumnIndexOrThrow("created_at_ms"));

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

        List<WorkoutStep> steps = new ArrayList<>();
        for (int i = 0; i < exerciseIds.size(); i++) {
            steps.add(new WorkoutStep(exerciseIds.get(i), workSeconds.get(i), restSeconds.get(i)));
        }

        return new Workout(id, name, sets, steps, createdAtMs);
    }

    @Override
    public void seedData() {
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        long count = 0;
        try (Cursor cursor = db.query("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_WORKOUT)) {
            if (cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
        }

        if (count == 0) {
            List<Workout> defaultWorkouts = DefaultWorkoutSeedData.createDefaultWorkouts();
            db.beginTransaction();
            try {
                for (Workout w : defaultWorkouts) {
                    saveWorkoutInternal(db, w);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
