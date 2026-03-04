package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.Workout;
import com.example.exergen.business.repository.IWorkoutRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutRepositorySQLite implements IWorkoutRepository {
    private final DatabaseHelper dbHelper;
    private final Context context;

    public WorkoutRepositorySQLite(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public void saveWorkout(Workout workout) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Convert the string list to a comma-separated string
        String exerciseIdsStr = String.join(",", workout.getExerciseIds());

        // Convert Integer lists to comma-separated strings
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
        values.put("rounds", workout.getRounds());
        values.put("exercise_ids", exerciseIdsStr);
        values.put("work_seconds", workSecStr.toString());
        values.put("rest_seconds", restSecStr.toString());

        // .replace() acts as an "update" (inserts if new, updates if it already exists)
        db.replace(DatabaseHelper.TABLE_WORKOUT, null, values);
    }

    @Override
    public Workout getWorkoutById(String workoutId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Workout workout = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_WORKOUT, null, "id = ?",
                new String[]{workoutId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            workout = parseCursorToWorkout(cursor);
            cursor.close();
        }
        return workout;
    }

    @Override
    public List<Workout> getAllWorkouts() {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_WORKOUT, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                workouts.add(parseCursorToWorkout(cursor));
            }
            while (cursor.moveToNext());
        }
        return workouts;
    }

    // Helper method to keep parsing logic clean
    private Workout parseCursorToWorkout(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int rounds = cursor.getInt(cursor.getColumnIndexOrThrow("rounds"));
        String exIdsStr = cursor.getString(cursor.getColumnIndexOrThrow("exercise_ids"));
        String workSecStr = cursor.getString(cursor.getColumnIndexOrThrow("work_seconds"));
        String restSecStr = cursor.getString(cursor.getColumnIndexOrThrow("rest_seconds"));

        List<String> exerciseIds = Arrays.asList(exIdsStr.split(","));

        List<Integer> workSeconds = new ArrayList<>();
        for (String s : workSecStr.split(",")) {
            if (!s.trim().isEmpty()) workSeconds.add(Integer.parseInt(s.trim()));
        }

        List<Integer> restSeconds = new ArrayList<>();
        for (String s : restSecStr.split(",")) {
            if (!s.trim().isEmpty()) restSeconds.add(Integer.parseInt(s.trim()));
        }

        return new Workout(id, name, rounds, exerciseIds, workSeconds, restSeconds);
    }

    @Override
    public void seedData() {
        if (getAllWorkouts().isEmpty()) {
            List<Workout> defaultWorkouts = loadWorkoutsFromAssets();
            for (Workout w : defaultWorkouts) {
                saveWorkout(w);
            }
        }
    }

    private List<Workout> loadWorkoutsFromAssets() {
        List<Workout> list = new ArrayList<>();
        try (InputStream is = context.getAssets().open("workouts.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 6) {
                    List<String> exIds = Arrays.asList(tokens[3].split("\\|"));

                    // Work seconds
                    List<Integer> workSecs = new ArrayList<>();
                    for (String s : tokens[4].split("\\|")) {
                        workSecs.add(Integer.parseInt(s));
                    }

                    // Rest seconds
                    List<Integer> restSecs = new ArrayList<>();
                    for (String s : tokens[5].split("\\|")) {
                        restSecs.add(Integer.parseInt(s));
                    }

                    // Add the workout to the list
                    list.add(new Workout(
                            tokens[0], tokens[1], Integer.parseInt(tokens[2]),
                            exIds, workSecs, restSecs
                    ));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}