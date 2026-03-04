package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseRepositorySQLite implements IExerciseRepository {
    private final DatabaseHelper dbHelper;
    private final Context context;

    public ExerciseRepositorySQLite(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }


    @Override
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String muscleGroupStr = cursor.getString(cursor.getColumnIndexOrThrow("muscle_groups"));
                    String equipmentStr = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
                    String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                    int intensity = cursor.getInt(cursor.getColumnIndexOrThrow("intensity"));
                    String imgName = cursor.getString(cursor.getColumnIndexOrThrow("image_name"));

                    List<String> muscleGroups = Arrays.asList(muscleGroupStr.split(","));
                    List<String> equipment = Arrays.asList(equipmentStr.split(","));

                    exercises.add(new Exercise(id, name, muscleGroups, equipment, instructions, intensity, imgName));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public Exercise getExerciseById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Exercise exercise = null;

        // Use try-with-resources to ensure the cursor closes automatically
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "id = ?",
                new String[]{id}, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String muscleGroupStr = cursor.getString(cursor.getColumnIndexOrThrow("muscle_groups"));
                String equipmentStr = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                int intensity = cursor.getInt(cursor.getColumnIndexOrThrow("intensity"));
                String imgName = cursor.getString(cursor.getColumnIndexOrThrow("image_name"));

                List<String> muscleGroups = Arrays.asList(muscleGroupStr.split(","));
                List<String> equipment = Arrays.asList(equipmentStr.split(","));

                exercise = new Exercise(id, name, muscleGroups, equipment, instructions, intensity, imgName);
            }
        }
        return exercise;
    }

    @Override
    public void insertExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String muscleGroupsStr = String.join(",", exercise.getMuscleGroups());
        String equipmentStr = String.join(",", exercise.getEquipment());

        values.put("id", exercise.getId());
        values.put("name", exercise.getName());
        values.put("muscle_groups", muscleGroupsStr);
        values.put("equipment", equipmentStr);
        values.put("instructions", exercise.getInstructions());
        values.put("intensity", exercise.getIntensity());
        values.put("image_name", exercise.getImageName());

        db.insert(DatabaseHelper.TABLE_EXERCISE, null, values);
    }

    @Override
    public void deleteExercise(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, "id = ?", new String[]{id});
    }

    @Override
    public void seedData() {
        if (getAllExercises().isEmpty()) {
            List<Exercise> defaultExercises = loadExercisesFromAssets();
            for (Exercise ex : defaultExercises) {
                insertExercise(ex);
            }
        }
    }

    // Load exercises from assets (app/assets/exercises.csv)
    private List<Exercise> loadExercisesFromAssets() {
        List<Exercise> list = new ArrayList<>();

        try (InputStream is = context.getAssets().open("exercises.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 7) {
                    List<String> muscles = Arrays.asList(tokens[2].split("\\|"));
                    List<String> equipment = Arrays.asList(tokens[3].split("\\|"));

                    list.add(new Exercise(
                            tokens[0], tokens[1], muscles, equipment, tokens[4], Integer.parseInt(tokens[5]), tokens[6]
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