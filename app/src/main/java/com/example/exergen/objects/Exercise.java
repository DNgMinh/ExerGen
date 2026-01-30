package com.example.exergen.objects;

public class Exercise {
    private String name;
    private int durationSeconds;

    public Exercise(String name, int durationSeconds) {
        this.name = name;
        this.durationSeconds = durationSeconds;
    }

    public String getName() {
        return name;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}