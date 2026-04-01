package com.example.exergen.model;

import java.util.List;
import java.util.ArrayList;

public class Workout {

    private final String id;
    private final String name;
    private final int sets;
    private final long createdAtMs;

    private final List<WorkoutStep> steps;

    public Workout(String id, String name, int sets, List<WorkoutStep> steps) {
        this(id, name, sets, steps, System.currentTimeMillis());
    }

    public Workout(String id, String name, int sets, List<WorkoutStep> steps, long createdAtMs) {
        this.id = ModelValidation.requireNonBlank(id, "ID required");
        this.name = ModelValidation.requireNonBlank(name, "Workout name required");
        if (sets <= 0)
            throw new IllegalArgumentException("Sets must be > 0");
        ModelValidation.requireNonEmptyList(steps, "steps required");
        this.sets = sets;
        this.steps = List.copyOf(steps);
        this.createdAtMs = createdAtMs;
    }

    public Workout(String id,
            String name,
            int sets,
            List<String> exerciseIds,
            List<Integer> workSeconds,
            List<Integer> restSeconds) {
        this(id, name, sets, buildSteps(exerciseIds, workSeconds, restSeconds), System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public long getCreatedAtMs() {
        return createdAtMs;
    }

    public List<WorkoutStep> getSteps() {
        return steps;
    }

    public List<String> getExerciseIds() {
        List<String> ids = new ArrayList<>();
        for (WorkoutStep step : steps) {
            ids.add(step.getExerciseId());
        }
        return List.copyOf(ids);
    }

    public List<Integer> getWorkSeconds() {
        List<Integer> values = new ArrayList<>();
        for (WorkoutStep step : steps) {
            values.add(step.getWorkSeconds());
        }
        return List.copyOf(values);
    }

    public List<Integer> getRestSeconds() {
        List<Integer> values = new ArrayList<>();
        for (WorkoutStep step : steps) {
            values.add(step.getRestSeconds());
        }
        return List.copyOf(values);
    }

    private static List<WorkoutStep> buildSteps(
            List<String> exerciseIds,
            List<Integer> workSeconds,
            List<Integer> restSeconds) {
        ModelValidation.requireNonEmptyList(exerciseIds, "exerciseIds required");
        ModelValidation.requireNonEmptyList(workSeconds, "workSeconds required");
        ModelValidation.requireNonEmptyList(restSeconds, "restSeconds required");
        if (workSeconds.size() != exerciseIds.size()) {
            throw new IllegalArgumentException("WorkSeconds mismatch");
        }
        if (restSeconds.size() != exerciseIds.size()) {
            throw new IllegalArgumentException("RestSeconds mismatch");
        }

        List<WorkoutStep> built = new ArrayList<>();
        for (int i = 0; i < exerciseIds.size(); i++) {
            built.add(new WorkoutStep(exerciseIds.get(i), workSeconds.get(i), restSeconds.get(i)));
        }
        return built;
    }
}
