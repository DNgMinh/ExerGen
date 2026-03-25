package com.example.exergen.business.service;

import com.example.exergen.business.exception.ExerciseNotFoundException;
import com.example.exergen.business.exception.SessionCompletedException;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;
import com.example.exergen.persistence.repository.IExerciseRepository;

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
            throw new SessionCompletedException();
        }

        WorkoutStep step = workout.getSteps().get(currentStep);
        String id = step.getExerciseId();
        Exercise ex = exerciseRepository.getExerciseById(id);

        if (ex == null) {
            throw new ExerciseNotFoundException(id);
        }
        return ex;
    }

    public void next() {
        if (!isFinished()) {
            currentStep++;
        }
    }

    public boolean isFinished() {
        return currentStep >= workout.getSteps().size();
    }

    public int getCurrentStepIndex() {
        return currentStep;
    }

    public int totalSteps() {
        return workout.getSteps().size();
    }
}