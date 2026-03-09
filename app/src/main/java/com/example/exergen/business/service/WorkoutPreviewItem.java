package com.example.exergen.business.service;

public final class WorkoutPreviewItem {
    private final int sequence;
    private final String exerciseName;
    private final int workSeconds;
    private final int restSeconds;

    public WorkoutPreviewItem(int sequence, String exerciseName, int workSeconds, int restSeconds) {
        this.sequence = sequence;
        this.exerciseName = exerciseName;
        this.workSeconds = workSeconds;
        this.restSeconds = restSeconds;
    }

    public int getSequence() {
        return sequence;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public int getRestSeconds() {
        return restSeconds;
    }
}
