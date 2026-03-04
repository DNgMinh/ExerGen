package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.business.repository.IExerciseRepository;

public class SessionManager {

    private final Workout workout;
    private final IExerciseRepository exerciseRepository;

    private int currentStep = 0;

    public SessionManager(Workout workout, IExerciseRepository exerciseRepository) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout required.");
        }
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository required.");
        }

        this.workout = workout;
        this.exerciseRepository = exerciseRepository;
    }

    public Exercise getCurrentExercise() {
        if (isFinished()) {
            return null;
        }

        String id = workout.getExerciseIds().get(currentStep);
        return exerciseRepository.getExerciseById(id);
    }

    public void next() {
        if (!isFinished()) {
            currentStep++;
        }
    }

    public boolean isFinished() {
        return currentStep >= workout.getExerciseIds().size();
    }

    public int getCurrentStepIndex() {
        return currentStep;
    }

    public int totalSteps() {
        return workout.getExerciseIds().size();
    }
}