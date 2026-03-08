package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;

import java.util.List;

public class ExerciseService {
    private final IExerciseRepository exerciseRepository;

    public ExerciseService(IExerciseRepository exerciseRepository) {
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository required.");
        }
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise getExerciseById(String id) {
        if (id == null || id.isEmpty()) {
            return null; // Return null if ID is invalid
        }
        return exerciseRepository.getExerciseById(id);
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.getAllExercises();
    }

    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise required.");
        }

        if (exerciseRepository.getExerciseById(exercise.getId()) != null) {
            throw new IllegalArgumentException("Duplicate exercise id detected.");
        }

        exerciseRepository.insertExercise(exercise);
    }

    public List<Exercise> filterByEquipment(String equipment) {
        if (equipment == null || equipment.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment required.");
        }
        return exerciseRepository.filterByEquipment(equipment.trim());
    }

    public List<Exercise> filterByMuscleGroup(String muscle) {
        if (muscle == null || muscle.trim().isEmpty()) {
            throw new IllegalArgumentException("Muscle required.");
        }
        return exerciseRepository.filterByMuscleGroup(muscle.trim());
    }
}