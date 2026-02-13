package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.persistence.repository.ExerciseRepository;

// Manages the state of an active workout session
public class SessionManager {

    private final Workout workout;
    private final ExerciseRepository exerciseRepository;

    private int currentStep = 0;

    // Initializes a new session for the given workout
    public SessionManager(Workout workout, ExerciseRepository exerciseRepository) {
        if (workout == null) {
            throw new IllegalArgumentException("Workout required.");
        }
        if (exerciseRepository == null) {
            throw new IllegalArgumentException("exerciseRepository required.");
        }

        this.workout = workout;
        this.exerciseRepository = exerciseRepository;
    }

    // Retrieve the exercise for the current step
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
