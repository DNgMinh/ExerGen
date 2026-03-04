package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.business.repository.IExerciseRepository;

import java.util.ArrayList;
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
        if (equipment == null || equipment.isEmpty()) {
            throw new IllegalArgumentException("Equipment required.");
        }

        List<Exercise> all = exerciseRepository.getAllExercises();
        List<Exercise> result = new ArrayList<>();
        for (Exercise ex : all) {
            if (ex.getEquipment().contains(equipment)) {
                result.add(ex);
            }
        }
        return result;
    }

    public List<Exercise> filterByMuscleGroup(String muscle) {
        if (muscle == null || muscle.isEmpty()) {
            throw new IllegalArgumentException("Muscle required.");
        }

        List<Exercise> all = exerciseRepository.getAllExercises();
        List<Exercise> result = new ArrayList<>();
        for (Exercise ex : all) {
            if (ex.getMuscleGroups().contains(muscle)) {
                result.add(ex);
            }
        }
        return result;
    }
}