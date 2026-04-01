package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExerciseRepositorySQLite implements IExerciseRepository {
    private static final String TAG = "ExerciseRepoSQLite";
    private final DatabaseHelper dbHelper;
    private final Context context;
    private final IEnumMapper enumMapper;

    public ExerciseRepositorySQLite(Context context, IEnumMapper enumMapper) {
        this(context, DatabaseHelper.DEFAULT_DATABASE_NAME, enumMapper);
    }

    public ExerciseRepositorySQLite(Context context, String databaseName, IEnumMapper enumMapper) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context, databaseName);
        this.enumMapper = enumMapper;
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
        insertExerciseInternal(db, exercise);
    }

    private void insertExerciseInternal(SupportSQLiteDatabase db, Exercise exercise) {
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
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();
        long count = 0;
        try (Cursor cursor = db.query("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_EXERCISE)) {
            if (cursor.moveToFirst()) {
                count = cursor.getLong(0);
            }
        }

        if (count == 0) {
            Log.d(TAG, "Database empty. Seeding exercises from assets...");
            List<Exercise> defaultExercises = loadExercisesFromAssets();
            
            db.beginTransaction();
            try {
                for (Exercise ex : defaultExercises) {
                    insertExerciseInternal(db, ex);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            Log.d(TAG, "Seeding complete. Inserted " + defaultExercises.size() + " exercises.");
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

        List<MuscleGroup> cleanMuscles = enumMapper.toMuscleEnums(muscleGroups);
        List<EquipmentType> cleanEquipment = enumMapper.toEquipmentEnums(equipment);

        return new Exercise(id, name, cleanMuscles, cleanEquipment, instructions, intensity, imagePaths);
    }

    private List<Exercise> loadExercisesFromAssets() {
        List<Exercise> list = new ArrayList<>();
        List<String[]> csvRows = CSVParser.parseAssetCSV(context, "exercises.csv");

        // Cache all images by folder name once to avoid repeated AssetManager.list() calls
        Map<String, List<String>> imageCache = new HashMap<>();
        try {
            String[] folders = context.getAssets().list("exercise");
            if (folders != null) {
                for (String folder : folders) {
                    String path = "exercise/" + folder;
                    String[] files = context.getAssets().list(path);
                    if (files != null) {
                        List<String> paths = new ArrayList<>();
                        for (String f : files) {
                            if (f.endsWith(".jpg") || f.endsWith(".png")) {
                                paths.add(path + "/" + f);
                            }
                        }
                        imageCache.put(folder, paths);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to scan asset folders", e);
        }

        for (String[] tokens : csvRows) {
            if (tokens.length >= 6) {
                String imageFolder = tokens[1].toLowerCase().replaceAll("[^a-z0-9]+", "_");
                if (imageFolder.endsWith("_")) {
                    imageFolder = imageFolder.substring(0, imageFolder.length() - 1);
                }

                List<String> imagePaths = imageCache.get(imageFolder);
                if (imagePaths == null || imagePaths.isEmpty()) {
                    imagePaths = List.of("placeholder.png");
                }

                List<MuscleGroup> cleanMuscles = enumMapper.toMuscleEnums(Arrays.asList(tokens[2].split("\\|")));
                List<EquipmentType> cleanEquipment = enumMapper.toEquipmentEnums(Arrays.asList(tokens[3].split("\\|")));

                list.add(new Exercise(
                        tokens[0], tokens[1], cleanMuscles, cleanEquipment, tokens[4],
                        Integer.parseInt(tokens[5]), imagePaths
                ));
            }
        }
        return list;
    }
}
