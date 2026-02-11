package com.example.exergen.business.model;

import com.example.exergen.persistence.repository.ExerciseRepository;

public class SessionManager {

    private final Workout workout;
    private final ExerciseRepository exerciseRepository;

    private int currentStep = 0;

    public SessionManager(Workout workout, ExerciseRepository exerciseRepository) {
        if (workout == null)
            throw new IllegalArgumentException("workout required");
        if (exerciseRepository == null)
            throw new IllegalArgumentException("exerciseRepository required");

        this.workout = workout;
        this.exerciseRepository = exerciseRepository;
    }

    // --- Core logic ---------------------------------------------------------

    public Exercise getCurrentExercise() {
        if (isFinished())
            return null;

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

    // --- Helpers ------------------------------------------------------------

    public int getCurrentStepIndex() {
        return currentStep;
    }

    public int totalSteps() {
        return workout.getExerciseIds().size();
    }
}
