package com.example.exergen.model;

public class SessionRecord {
    private final String id;
    private final String workoutId;
    private final String workoutName;
    private final long completedAtEpochMs;
    private final int totalDurationSeconds;
    private final int exerciseCount;
    private final int roundsPlanned;
    private final int roundsCompleted;

    public SessionRecord(
            String id,
            String workoutId,
            String workoutName,
            long completedAtEpochMs,
            int totalDurationSeconds,
            int exerciseCount,
            int roundsPlanned,
            int roundsCompleted) {
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
        if (roundsPlanned <= 0) {
            throw new IllegalArgumentException("Rounds planned must be > 0");
        }
        if (roundsCompleted < 0) {
            throw new IllegalArgumentException("Rounds completed must be >= 0");
        }
        if (roundsCompleted > roundsPlanned) {
            throw new IllegalArgumentException("Rounds completed cannot exceed rounds planned");
        }

        this.completedAtEpochMs = completedAtEpochMs;
        this.totalDurationSeconds = totalDurationSeconds;
        this.exerciseCount = exerciseCount;
        this.roundsPlanned = roundsPlanned;
        this.roundsCompleted = roundsCompleted;
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

    public int getRoundsPlanned() {
        return roundsPlanned;
    }

    public int getRoundsCompleted() {
        return roundsCompleted;
    }
}
