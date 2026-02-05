package com.example.exergen.persistence;

import com.example.exergen.business.Exercise;
import com.example.exergen.business.Workout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutPersistenceStub {
    private List<Workout> data;

    public WorkoutPersistenceStub() {
        data = new ArrayList<>();

        // Create a fake workout
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(
                "ex-1",
                "Jumping Jacks",
                "Stand tall, jump feet out while raising arms overhead, then return.",
                Arrays.asList("Bodyweight"),
                Arrays.asList("Full Body"),
                20,
                3));
        exercises.add(new Exercise(
                "ex-2",
                "Rest",
                "Recover and breathe steadily.",
                Arrays.asList("None"),
                Arrays.asList("Recovery"),
                10,
                1));
        exercises.add(new Exercise(
                "ex-3",
                "Pushups",
                "Keep a straight line from head to heels and lower with control.",
                Arrays.asList("Bodyweight"),
                Arrays.asList("Chest", "Triceps"),
                20,
                4));

        // Add it to the list
        data.add(new Workout("Quick Tabata", exercises));
    }

    public List<Workout> getWorkouts() {
        return data;
    }
}