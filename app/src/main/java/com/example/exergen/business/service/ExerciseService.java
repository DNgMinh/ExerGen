package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.business.exception.DuplicateExerciseException;
import com.example.exergen.business.exception.InvalidFilterException;
import com.example.exergen.persistence.repository.IExerciseRepository;
import com.example.exergen.model.MuscleGroup;

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
            throw new DuplicateExerciseException(exercise.getId());
        }

        exerciseRepository.insertExercise(exercise);
    }

    public List<Exercise> filterByEquipment(EquipmentType equipment) {
        if (equipment == null) {
            throw new InvalidFilterException("Equipment required.");
        }
        return exerciseRepository.filterByEquipment(equipment);
    }

    public List<Exercise> filterByMuscleGroup(MuscleGroup muscle) {
        if (muscle == null) {
            throw new InvalidFilterException("Muscle group required.");
        }
        return exerciseRepository.filterByMuscleGroup(muscle);
    }

    public List<Exercise> filterByConstraints(List<EquipmentType> equipment, List<MuscleGroup> muscleGroups) {
        List<Exercise> all = exerciseRepository.getAllExercises();
        List<Exercise> filtered = new ArrayList<>();

        for (Exercise exercise : all) {
            if (ExerciseConstraintMatcher.matches(exercise, equipment, muscleGroups)) {
                filtered.add(exercise);
            }
        }
        return filtered;
    }
}