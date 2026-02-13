package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.repository.ExerciseRepository;

import java.util.List;

public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository required");
        }
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise getExerciseById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id required");
        }
        return exerciseRepository.getExerciseById(id);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.getAllExercises();
    }

    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise required");
        }
        exerciseRepository.addExercise(exercise);
    }

    public List<Exercise> filterByEquipment(String equipment) {
        if (equipment == null || equipment.isEmpty()) {
            throw new IllegalArgumentException("equipment required");
        }
        return exerciseRepository.filterByEquipment(equipment);
    }

    public List<Exercise> filterByMuscleGroup(String muscle) {
        if (muscle == null || muscle.isEmpty()) {
            throw new IllegalArgumentException("muscle required");
        }
        return exerciseRepository.filterByMuscleGroup(muscle);
    }
}
