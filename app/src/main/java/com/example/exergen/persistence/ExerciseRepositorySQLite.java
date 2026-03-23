package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.repository.IExerciseRepository;
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

    public ExerciseRepositorySQLite(Context context) {
        this(context, DatabaseHelper.DEFAULT_DATABASE_NAME);
    }

    public ExerciseRepositorySQLite(Context context, String databaseName) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context, databaseName);
    }

    @Override
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor cursor = db.query("SELECT * FROM " + DatabaseHelper.TABLE_EXERCISE)) {
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
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        Exercise exercise = null;

        try (Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(DatabaseHelper.TABLE_EXERCISE)
                .selection("id = ?", new Object[]{id})
                .create())) {

            if (cursor.moveToFirst()) {
                exercise = mapCursorToExercise(cursor);
            }
        }
        return exercise;
    }

    @Override
    public List<Exercise> filterByEquipment(EquipmentType equipment) {
        List<Exercise> exercises = new ArrayList<>();
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchString = equipment.getLabel();

        try (Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(DatabaseHelper.TABLE_EXERCISE)
                .selection("equipment LIKE ?", new Object[]{"%" + searchString + "%"})
                .create())) {
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
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        String searchString = muscleGroup.getLabel();

        try (Cursor cursor = db.query(SupportSQLiteQueryBuilder.builder(DatabaseHelper.TABLE_EXERCISE)
                .selection("muscle_groups LIKE ?", new Object[]{"%" + searchString + "%"})
                .create())) {
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
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
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

        db.insert(DatabaseHelper.TABLE_EXERCISE, SQLiteDatabase.CONFLICT_REPLACE, values);
    }

    @Override
    public void deleteExercise(String id) {
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_EXERCISE, "id = ?", new Object[]{id});
    }

    @Override
    public void seedData() {
        SupportSQLiteDatabase db = dbHelper.getReadableDatabase();
        long count = 0;
        try (Cursor cursor = db.query("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_EXERCISE)) {
            if (cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
        }

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

        List<MuscleGroup> cleanMuscles = EnumMapper.toMuscleEnums(muscleGroups);
        List<EquipmentType> cleanEquipment = EnumMapper.toEquipmentEnums(equipment);

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

                if (imagePaths.isEmpty()) {
                    imagePaths.add("placeholder.png");
                }

                String instructions = tokens[4].replaceAll("^\"|\"$", "");

                List<MuscleGroup> cleanMuscles = EnumMapper.toMuscleEnums(muscles);
                List<EquipmentType> cleanEquipment = EnumMapper.toEquipmentEnums(equipment);

                list.add(new Exercise(
                        tokens[0], tokens[1], cleanMuscles, cleanEquipment, instructions,
                        Integer.parseInt(tokens[5]), imagePaths
                ));
            }
        }
        return list;
    }
}
