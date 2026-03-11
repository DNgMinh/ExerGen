package com.example.exergen.model;

import java.util.Objects;
public class WorkoutSet {
    private final String id;
    private final Exercise exercise;
    private int reps;
    private double weight;
    private boolean completed;

    public WorkoutSet(String id, Exercise exercise, int reps, double weight, boolean completed) {
        this.id = ModelValidation.requireNonBlank(id, "ID required");
        this.exercise = Objects.requireNonNull(exercise, "Exercise cannot be null");
        if (reps < 0)
            throw new IllegalArgumentException("Reps must be >= 0");
        if (weight < 0)
            throw new IllegalArgumentException("Weight must be >= 0");
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }
    public Exercise getExercise() {
        return exercise;
    }
    public int getReps() {
        return reps;
    }
    public double getWeight() {
        return weight;
    }
    public boolean isCompleted() {
        return completed;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
