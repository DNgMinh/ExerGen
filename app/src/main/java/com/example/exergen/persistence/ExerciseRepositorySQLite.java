package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseRepositorySQLite implements IExerciseRepository {
    private static final String TAG = "ExerciseRepoSQLite";
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
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(mapCursorToExercise(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public Exercise getExerciseById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Exercise exercise = null;

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "id = ?",
                new String[]{id}, null, null, null)) {

            if (cursor.moveToFirst()) {
                exercise = mapCursorToExercise(cursor);
            }
        }
        return exercise;
    }

    @Override
    public List<Exercise> filterByEquipment(String equipment) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "equipment LIKE ?",
                new String[]{"%" + equipment + "%"}, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(mapCursorToExercise(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public List<Exercise> filterByMuscleGroup(String muscleGroup) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "muscle_groups LIKE ?",
                new String[]{"%" + muscleGroup + "%"}, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(mapCursorToExercise(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public void insertExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String muscleGroupsStr = String.join(",", exercise.getMuscleGroups());
        String equipmentStr = String.join(",", exercise.getEquipment());
        String imagePathsStr = String.join(",", exercise.getImagePaths());

        values.put("id", exercise.getId());
        values.put("name", exercise.getName());
        values.put("muscle_groups", muscleGroupsStr);
        values.put("equipment", equipmentStr);
        values.put("instructions", exercise.getInstructions());
        values.put("intensity", exercise.getIntensity());
        values.put("image_paths", imagePathsStr);

        db.insert(DatabaseHelper.TABLE_EXERCISE, null, values);
    }

    @Override
    public void deleteExercise(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, "id = ?", new String[]{id});
    }

    @Override
    public void seedData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, null, null);

        List<Exercise> defaultExercises = loadExercisesFromAssets();
        for (Exercise ex : defaultExercises) {
            insertExercise(ex);
        }
    }

    private Exercise mapCursorToExercise(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String muscleGroupStr = cursor.getString(cursor.getColumnIndexOrThrow("muscle_groups"));
        String equipmentStr = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
        String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
        int intensity = cursor.getInt(cursor.getColumnIndexOrThrow("intensity"));
        String imagePathsStr = cursor.getString(cursor.getColumnIndexOrThrow("image_paths"));

        List<String> muscleGroups = Arrays.asList(muscleGroupStr.split(","));
        List<String> equipment = Arrays.asList(equipmentStr.split(","));
        List<String> imagePaths = Arrays.asList(imagePathsStr.split(","));

        return new Exercise(id, name, muscleGroups, equipment, instructions, intensity, imagePaths);
    }

    private List<Exercise> loadExercisesFromAssets() {
        List<Exercise> list = new ArrayList<>();
        List<String[]> csvRows = CSVParser.parseAssetCSV(context, "exercises.csv");

        for (String[] tokens : csvRows) {
            if (tokens.length >= 7) {
                List<String> muscles = Arrays.asList(tokens[2].split("\\|"));
                List<String> equipment = Arrays.asList(tokens[3].split("\\|"));
                
                String imageFolder = tokens[6];
                List<String> imagePaths = new ArrayList<>();
                
                try {
                    String assetsPath = "exercise/" + imageFolder;
                    String[] files = context.getAssets().list(assetsPath);
                    if (files != null) {
                        for (String file : files) {
                            if (file.endsWith(".jpg") || file.endsWith(".png")) {
                                imagePaths.add(assetsPath + "/" + file);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Could not list images for folder: " + imageFolder, e);
                }

                // Fallback if no images found
                if (imagePaths.isEmpty()) {
                    imagePaths.add("placeholder.png");
                }

                String instructions = tokens[4].replaceAll("^\"|\"$", "");

                list.add(new Exercise(
                        tokens[0], tokens[1], muscles, equipment, instructions,
                        Integer.parseInt(tokens[5]), imagePaths
                ));
            }
        }
        return list;
    }
}
