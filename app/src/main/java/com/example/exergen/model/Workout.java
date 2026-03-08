package com.example.exergen.model;

import java.util.List;

public class Workout {

    private final String id;
    private final String name;
    private final int rounds;

    private final List<String> exerciseIds;
    private final List<Integer> workSeconds;
    private final List<Integer> restSeconds;

    public Workout(String id,
            String name,
            int rounds,
            List<String> exerciseIds,
            List<Integer> workSeconds,
            List<Integer> restSeconds) {
        this.id = ModelValidation.requireNonBlank(id, "ID required");
        this.name = ModelValidation.requireNonBlank(name, "Workout name required");
        if (rounds <= 0)
            throw new IllegalArgumentException("Rounds must be > 0");
        ModelValidation.requireNonEmptyList(exerciseIds, "exerciseIds required");
        ModelValidation.requireNonEmptyList(workSeconds, "workSeconds required");
        ModelValidation.requireNonEmptyList(restSeconds, "restSeconds required");
        if (workSeconds.size() != exerciseIds.size())
            throw new IllegalArgumentException("WorkSeconds mismatch");
        if (restSeconds.size() != exerciseIds.size())
            throw new IllegalArgumentException("RestSeconds mismatch");
        this.rounds = rounds;
        this.exerciseIds = List.copyOf(exerciseIds);
        this.workSeconds = List.copyOf(workSeconds);
        this.restSeconds = List.copyOf(restSeconds);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRounds() {
        return rounds;
    }

    public List<String> getExerciseIds() {
        return exerciseIds;
    }

    public List<Integer> getWorkSeconds() {
        return workSeconds;
    }

    public List<Integer> getRestSeconds() {
        return restSeconds;
    }
}
