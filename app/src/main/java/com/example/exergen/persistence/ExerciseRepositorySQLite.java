package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;

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
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(extractExerciseFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public List<Exercise> filterByEquipment(String equipment) {
        if (equipment == null || equipment.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment required.");
        }

        String normalizedEquipment = equipment.trim();
        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : getAllExercises()) {
            for (String currentEquipment : exercise.getEquipment()) {
                if (currentEquipment.equalsIgnoreCase(normalizedEquipment)) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<Exercise> filterByMuscleGroup(String muscleGroup) {
        if (muscleGroup == null || muscleGroup.trim().isEmpty()) {
            throw new IllegalArgumentException("Muscle required.");
        }

        String normalizedMuscleGroup = muscleGroup.trim();
        List<Exercise> result = new ArrayList<>();
        for (Exercise exercise : getAllExercises()) {
            for (String currentMuscleGroup : exercise.getMuscleGroups()) {
                if (currentMuscleGroup.equalsIgnoreCase(normalizedMuscleGroup)) {
                    result.add(exercise);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Exercise getExerciseById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Exercise exercise = null;

        // Use try-with-resources to ensure the cursor closes automatically
        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "id = ?",
                new String[]{id}, null, null, null)) {

            if (cursor.moveToFirst()) {
                exercise = extractExerciseFromCursor(cursor);
            }
        }
        return exercise;
    }

    private Exercise extractExerciseFromCursor(Cursor cursor) {
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

    @Override
    public void insertExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", exercise.getId());
        values.put("name", exercise.getName());
        values.put("muscle_groups", String.join(",", exercise.getMuscleGroups()));
        values.put("equipment", String.join(",", exercise.getEquipment()));
        values.put("instructions", exercise.getInstructions());
        values.put("intensity", exercise.getIntensity());
        values.put("image_paths", String.join(",", exercise.getImagePaths()));

        db.insertWithOnConflict(DatabaseHelper.TABLE_EXERCISE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void deleteExercise(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, "id = ?", new String[] { id });
    }

    @Override
    public void seedData() {
        // FOR DEVELOPMENT: Clear existing data so the new CSV is always loaded
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, null, null);

        List<Exercise> defaultExercises = loadExercisesFromAssets();
        for (Exercise ex : defaultExercises) {
            insertExercise(ex);
        }
    }

    // Load exercises from assets (app/assets/exercises.csv)
    private List<Exercise> loadExercisesFromAssets() {
        List<Exercise> list = new ArrayList<>();

        List<String[]> csvRows = CSVParser.parseAssetCSV(context, "exercises.csv");

        for (String[] tokens : csvRows) {
            if (tokens.length >= 7) {
                String id = tokens[0];
                String name = tokens[1];
                List<String> muscles = Arrays.asList(tokens[2].split("\\|"));
                List<String> equipment = Arrays.asList(tokens[3].split("\\|"));
                String instructions = tokens[4].replaceAll("^\"|\"$", "");
                int intensity = Integer.parseInt(tokens[5]);

                // image_folder is tokens[6]. We assume the structure: exercises/folder_name/0.jpg, exercises/folder_name/1.jpg
                String imageFolder = tokens[6];
                List<String> imagePaths = Arrays.asList(
                    "exercise/" + imageFolder + "/0.jpg",
                    "exercise/" + imageFolder + "/1.jpg"
                );

                list.add(new Exercise(id, name, muscles, equipment, instructions, intensity, imagePaths));
                list.add(new Exercise(
                        tokens[0], tokens[1], muscles, equipment, instructions,
                        Integer.parseInt(tokens[5]), tokens[6]));
            }
        }
        return list;
    }
}