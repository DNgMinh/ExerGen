package com.example.exergen.model;

public class SessionRecord {
    private final String id;
    private final String workoutId;
    private final String workoutName;
    private final long completedAtEpochMs;
    private final int totalDurationSeconds;
    private final int exerciseCount;
    private final int setsPlanned;
    private final int setsCompleted;

    public SessionRecord(
            String id,
            String workoutId,
            String workoutName,
            long completedAtEpochMs,
            int totalDurationSeconds,
            int exerciseCount,
            int setsPlanned,
            int setsCompleted) {
        this.id = ModelValidation.requireNonBlank(id, "Session id required");
        this.workoutId = ModelValidation.requireNonBlank(workoutId, "Workout id required");
        this.workoutName = ModelValidation.requireNonBlank(workoutName, "Workout name required");
        if (completedAtEpochMs <= 0L) {
            throw new IllegalArgumentException("Completion timestamp must be > 0");
        }
        if (totalDurationSeconds < 0) {
            throw new IllegalArgumentException("Total duration must be >= 0");
        }
        if (exerciseCount <= 0) {
            throw new IllegalArgumentException("Exercise count must be > 0");
        }
        if (setsPlanned <= 0) {
            throw new IllegalArgumentException("Sets planned must be > 0");
        }
        if (setsCompleted < 0) {
            throw new IllegalArgumentException("Sets completed must be >= 0");
        }
        if (setsCompleted > setsPlanned) {
            throw new IllegalArgumentException("Sets completed cannot exceed sets planned");
        }

        this.completedAtEpochMs = completedAtEpochMs;
        this.totalDurationSeconds = totalDurationSeconds;
        this.exerciseCount = exerciseCount;
        this.setsPlanned = setsPlanned;
        this.setsCompleted = setsCompleted;
    }

    public String getId() {
        return id;
    }

    public String getWorkoutId() {
        return workoutId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public long getCompletedAtEpochMs() {
        return completedAtEpochMs;
    }

    public int getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public int getSetsPlanned() {
        return setsPlanned;
    }

    public int getSetsCompleted() {
        return setsCompleted;
    }

}
