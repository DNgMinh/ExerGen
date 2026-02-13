package com.example.exergen.persistence.repository;

import com.example.exergen.model.Exercise;

import java.util.List;

public interface ExerciseRepository {

    Exercise getExerciseById(String id);

    List<Exercise> getAllExercises();

    List<Exercise> filterByEquipment(String equipment);

    List<Exercise> filterByMuscleGroup(String muscle);

    void addExercise(Exercise exercise);
}
