package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseRepositorySQLite implements IExerciseRepository {
    private static final String TAG = "ExerciseRepoSQLite";
    private final DatabaseHelper dbHelper;
    private final Context context;
    private final EnumMapper mapper;

    public ExerciseRepositorySQLite(Context context, EnumMapper mapper) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.mapper = mapper;
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
    public List<Exercise> filterByEquipment(EquipmentType equipment) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchString = equipment.getLabel();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "equipment LIKE ?",
                new String[]{"%" + searchString + "%"}, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(mapCursorToExercise(cursor));
                } while (cursor.moveToNext());
            }
        }
        return exercises;
    }

    @Override
    public List<Exercise> filterByMuscleGroup(MuscleGroup muscleGroup) {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchString = muscleGroup.getLabel();

        try (Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, "muscle_groups LIKE ?",
                new String[]{"%" + searchString + "%"}, null, null, null)) {
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

        String muscleGroupsStr = exercise.getMuscleGroups().stream().map(MuscleGroup::name).collect(Collectors.joining(","));
        String equipmentStr = exercise.getEquipment().stream().map(EquipmentType::name).collect(Collectors.joining(","));
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_EXERCISE);

        // Only seed if the database is currently empty
        if (count == 0) {
            Log.d(TAG, "Database empty. Seeding exercises from assets...");
            List<Exercise> defaultExercises = loadExercisesFromAssets();
            for (Exercise ex : defaultExercises) {
                insertExercise(ex);
            }
        } else {
            Log.d(TAG, "Database already contains " + count + " exercises. Skipping seed.");
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

        List<MuscleGroup> cleanMuscles = mapper.toMuscleEnums(muscleGroups);
        List<EquipmentType> cleanEquipment = mapper.toEquipmentEnums(equipment);

        return new Exercise(id, name, cleanMuscles, cleanEquipment, instructions, intensity, imagePaths);
    }

    private List<Exercise> loadExercisesFromAssets() {
        List<Exercise> list = new ArrayList<>();
        List<String[]> csvRows = CSVParser.parseAssetCSV(context, "exercises.csv");

        for (String[] tokens : csvRows) {
            if (tokens.length >= 7) {
                List<String> muscles = Arrays.asList(tokens[2].split("\\|"));
                List<String> equipment = Arrays.asList(tokens[3].split("\\|"));


                String imageFolder = tokens[1].toLowerCase().replaceAll("[^a-z0-9]+", "_");
                if (imageFolder.endsWith("_")) {
                    imageFolder = imageFolder.substring(0, imageFolder.length() - 1);
                }

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

                List<MuscleGroup> cleanMuscles = mapper.toMuscleEnums(muscles);
                List<EquipmentType> cleanEquipment = mapper.toEquipmentEnums(equipment);

                list.add(new Exercise(
                        tokens[0], tokens[1], cleanMuscles, cleanEquipment, instructions,
                        Integer.parseInt(tokens[5]), imagePaths
                ));
            }
        }
        return list;
    }
}