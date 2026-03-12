package com.example.exergen.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.exergen.application.helper.DatabaseHelper;
import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseRepositorySQLite implements IExerciseRepository {
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
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String muscleGroupStr = cursor.getString(cursor.getColumnIndexOrThrow("muscle_groups"));
                    String equipmentStr = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
                    String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                    int intensity = cursor.getInt(cursor.getColumnIndexOrThrow("intensity"));
                    String imgName = cursor.getString(cursor.getColumnIndexOrThrow("image_name"));

                    List<String> muscleGroups = Arrays.asList(muscleGroupStr.split(","));
                    List<String> equipment = Arrays.asList(equipmentStr.split(","));

                    List<MuscleGroup> cleanMuscles = mapper.toMuscleEnums(muscleGroups);
                    List<EquipmentType> cleanEquipment = mapper.toEquipmentEnums(equipment);

                    exercises.add(new Exercise(id, name, cleanMuscles, cleanEquipment, instructions, intensity, imgName));
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
                new String[] { id }, null, null, null)) {

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String muscleGroupStr = cursor.getString(cursor.getColumnIndexOrThrow("muscle_groups"));
                String equipmentStr = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                int intensity = cursor.getInt(cursor.getColumnIndexOrThrow("intensity"));
                String imgName = cursor.getString(cursor.getColumnIndexOrThrow("image_name"));

                List<String> muscleGroups = Arrays.asList(muscleGroupStr.split(","));
                List<String> equipment = Arrays.asList(equipmentStr.split(","));

                List<MuscleGroup> cleanMuscles = mapper.toMuscleEnums(muscleGroups);
                List<EquipmentType> cleanEquipment = mapper.toEquipmentEnums(equipment);

                exercise = new Exercise(id, name, cleanMuscles, cleanEquipment, instructions, intensity, imgName);
            }
        }
        return exercise;
    }

    @Override
    public void insertExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String muscleGroupsStr = exercise.getMuscleGroups().stream().map(MuscleGroup::name).collect(Collectors.joining(","));
        String equipmentStr = exercise.getEquipment().stream().map(EquipmentType::name).collect(Collectors.joining(","));

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
                List<String> muscles = Arrays.asList(tokens[2].split("\\|"));
                List<String> equipment = Arrays.asList(tokens[3].split("\\|"));

                String instructions = tokens[4].replaceAll("^\"|\"$", "");

                List<MuscleGroup> cleanMuscles = mapper.toMuscleEnums(muscles);
                List<EquipmentType> cleanEquipment = mapper.toEquipmentEnums(equipment);

                list.add(new Exercise(
                        tokens[0], tokens[1], cleanMuscles, cleanEquipment, instructions,
                        Integer.parseInt(tokens[5]), tokens[6]));
            }
        }
        return list;
    }
}