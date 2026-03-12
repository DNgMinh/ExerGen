package com.example.exergen.business.repository;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.List;

public interface IExerciseRepository {
    List<Exercise> getAllExercises();

    List<Exercise> filterByEquipment(String equipment);

    List<Exercise> filterByMuscleGroup(String muscleGroup);

    void insertExercise(Exercise exercise);

    // Get a single exercise by its database ID
    Exercise getExerciseById(String id);

    void deleteExercise(String id);

    void seedData();
}