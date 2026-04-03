package com.example.exergen.presentation;

import com.example.exergen.model.Exercise;

public class ExerciseListItem {
    private final Exercise exercise;
    private final String name;
    private final String attributes;

    public ExerciseListItem(Exercise exercise, String name, String attributes) {
        if (exercise == null) {
            throw new IllegalArgumentException("exercise required");
        }
        this.exercise = exercise;
        this.name = name == null ? "" : name;
        this.attributes = attributes == null ? "" : attributes;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return attributes;
    }
}
