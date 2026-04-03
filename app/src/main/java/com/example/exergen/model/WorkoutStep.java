package com.example.exergen.model;

public class WorkoutStep {
    private final String exerciseId;
    private final int workSeconds;
    private final int restSeconds;

    public WorkoutStep(String exerciseId, int workSeconds, int restSeconds) {
        this.exerciseId = ModelValidation.requireNonBlank(exerciseId, "exerciseId required");
        if (workSeconds < 0) {
            throw new IllegalArgumentException("workSeconds must be >= 0");
        }
        if (restSeconds < 0) {
            throw new IllegalArgumentException("restSeconds must be >= 0");
        }
        this.workSeconds = workSeconds;
        this.restSeconds = restSeconds;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public int getRestSeconds() {
        return restSeconds;
    }
}
