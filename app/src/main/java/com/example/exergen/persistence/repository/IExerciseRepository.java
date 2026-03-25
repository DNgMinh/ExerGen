package com.example.exergen.persistence.repository;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.List;

public interface IExerciseRepository {
    List<Exercise> getAllExercises();

    List<Exercise> filterByEquipment(EquipmentType equipment);

    List<Exercise> filterByMuscleGroup(MuscleGroup muscleGroup);

    void insertExercise(Exercise exercise);

    Exercise getExerciseById(String id);

    void deleteExercise(String id);

    void seedData();
}
