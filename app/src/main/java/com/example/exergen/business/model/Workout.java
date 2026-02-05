package com.example.exergen.business.model;

import java.util.List;

public class Workout {
    private String name;
    private List<Exercise> exerciseList;

    public Workout(String name, List<Exercise> exerciseList) {
        this.name = name;
        this.exerciseList = exerciseList;
    }

    public String getName() {
        return name;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }
}
