package com.example.exergen.persistence;

import com.example.exergen.objects.Exercise;
import com.example.exergen.objects.Workout;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPersistenceStub {
    private List<Workout> data;

    public WorkoutPersistenceStub() {
        data = new ArrayList<>();

        // Create a fake workout
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Jumping Jacks", 20));
        exercises.add(new Exercise("Rest", 10));
        exercises.add(new Exercise("Pushups", 20));

        // Add it to the list
        data.add(new Workout("Quick Tabata", exercises));
    }

    public List<Workout> getWorkouts() {
        return data;
    }
}