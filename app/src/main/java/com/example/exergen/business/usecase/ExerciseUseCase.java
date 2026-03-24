package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;

import java.util.List;

public class ExerciseUseCase {
    private final ExerciseService exerciseService;

    public ExerciseUseCase(ExerciseService exerciseService) {
        if (exerciseService == null) {
            throw new IllegalArgumentException("exerciseService required");
        }
        this.exerciseService = exerciseService;
    }

    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    public Exercise getExerciseById(String id) {
        return exerciseService.getExerciseById(id);
    }
}
