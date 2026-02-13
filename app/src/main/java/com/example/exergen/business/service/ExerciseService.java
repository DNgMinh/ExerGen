package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.repository.ExerciseRepository;
import java.util.List;

public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        // Ensure the repository dependency is not null
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository required.");
        }
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise getExerciseById(String id) {
        // Make sure the provided ID is valid
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID required");
        }
        // Return the specific exercise from the repository
        return exerciseRepository.getExerciseById(id);
    }

    public List<Exercise> getAllExercises() {
        // Return a list of all available exercises
        return exerciseRepository.getAllExercises();
    }

    public void addExercise(Exercise exercise) {
        // Ensure exercise is not null
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise required.");
        }

        // Ensure an exercise with this id does not already exist
        if (exerciseRepository.getExerciseById(exercise.getId()) != null) {
            throw new IllegalArgumentException("Duplicate exercise id detected.");
        }

        exerciseRepository.addExercise(exercise);
    }

    public List<Exercise> filterByEquipment(String equipment) {
        // Ensure the equipment parameter is valid
        if (equipment == null || equipment.isEmpty()) {
            throw new IllegalArgumentException("Equipment required.");
        }
        // Return only exercises that require this equipment
        return exerciseRepository.filterByEquipment(equipment);
    }

    public List<Exercise> filterByMuscleGroup(String muscle) {
        // Validate the muscle parameter
        if (muscle == null || muscle.isEmpty()) {
            throw new IllegalArgumentException("Muscle required.");
        }
        // Return only exercises that target this muscle group
        return exerciseRepository.filterByMuscleGroup(muscle);
    }
}
