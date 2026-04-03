package com.example.exergen.model;

public class SessionRecord {
    public static final int UNKNOWN_ESTIMATED_CALORIES = -1;

    private final String id;
    private final String workoutId;
    private final String workoutName;
    private final long completedAtEpochMs;
    private final int totalDurationSeconds;
    private final int exerciseCount;
    private final int setsPlanned;
    private final int setsCompleted;
    private final int estimatedCalories;

    public SessionRecord(
            String id,
            String workoutId,
            String workoutName,
            long completedAtEpochMs,
            int totalDurationSeconds,
            int exerciseCount,
            int setsPlanned,
            int setsCompleted) {
        this(
                id,
                workoutId,
                workoutName,
                completedAtEpochMs,
                totalDurationSeconds,
                exerciseCount,
                setsPlanned,
                setsCompleted,
                UNKNOWN_ESTIMATED_CALORIES);
    }

    public SessionRecord(
            String id,
            String workoutId,
            String workoutName,
            long completedAtEpochMs,
            int totalDurationSeconds,
            int exerciseCount,
            int setsPlanned,
            int setsCompleted,
            int estimatedCalories) {
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
        if (estimatedCalories < UNKNOWN_ESTIMATED_CALORIES) {
            throw new IllegalArgumentException("Estimated calories must be >= -1");
        }

        this.completedAtEpochMs = completedAtEpochMs;
        this.totalDurationSeconds = totalDurationSeconds;
        this.exerciseCount = exerciseCount;
        this.setsPlanned = setsPlanned;
        this.setsCompleted = setsCompleted;
        this.estimatedCalories = estimatedCalories;
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

    public int getEstimatedCalories() {
        return estimatedCalories;
    }

    public boolean hasEstimatedCalories() {
        return estimatedCalories >= 0;
    }

}
