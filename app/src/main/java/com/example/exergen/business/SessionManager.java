package com.example.exergen.business;

import com.example.exergen.objects.Exercise;
import com.example.exergen.objects.Workout;

public class SessionManager {
    private Workout currentWorkout;
    private int currentStepIndex = 0;

    public SessionManager(Workout workout) {
        this.currentWorkout = workout;
    }

    public Exercise getCurrentExercise() {
        if (currentStepIndex >= currentWorkout.getExerciseList().size()) {
            return null;
        }
        return currentWorkout.getExerciseList().get(currentStepIndex);
    }

    public void nextStep() {
        if (currentStepIndex < currentWorkout.getExerciseList().size()) {
            currentStepIndex++;
        }
    }

    public boolean isFinished() {
        return currentStepIndex >= currentWorkout.getExerciseList().size();
    }
}