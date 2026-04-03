package com.example.exergen.presentation;

import com.example.exergen.model.Workout;

public class WorkoutListItem {
    private final Workout workout;
    private final String name;
    private final String details;

    public WorkoutListItem(Workout workout, String name, String details) {
        if (workout == null) {
            throw new IllegalArgumentException("workout required");
        }
        this.workout = workout;
        this.name = name == null ? "" : name;
        this.details = details == null ? "" : details;
    }

    public Workout getWorkout() {
        return workout;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }
}
