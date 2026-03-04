package com.example.exergen.model;

import java.util.ArrayList;
import java.util.Collections;
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

        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("ID required");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Workout name required");
        if (rounds <= 0)
            throw new IllegalArgumentException("Rounds must be > 0");
        if (exerciseIds == null || exerciseIds.isEmpty())
            throw new IllegalArgumentException("exerciseIds required");
        if (workSeconds.size() != exerciseIds.size())
            throw new IllegalArgumentException("WorkSeconds mismatch");
        if (restSeconds.size() != exerciseIds.size())
            throw new IllegalArgumentException("RestSeconds mismatch");

        this.id = id;
        this.name = name;
        this.rounds = rounds;
        this.exerciseIds = Collections.unmodifiableList(new ArrayList<>(exerciseIds));
        this.workSeconds = Collections.unmodifiableList(new ArrayList<>(workSeconds));
        this.restSeconds = Collections.unmodifiableList(new ArrayList<>(restSeconds));
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

    // Calculate total duration of the working (in seconds)
    public int totalDurationSec() {
        int perRound = 0;
        for (int i = 0; i < exerciseIds.size(); i++) {
            perRound += workSeconds.get(i) + restSeconds.get(i);
        }
        return perRound * rounds;
    }
}

