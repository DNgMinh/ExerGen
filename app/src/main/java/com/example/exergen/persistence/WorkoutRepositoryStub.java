package com.example.exergen.persistence;

import com.example.exergen.business.repository.IWorkoutRepository;
import com.example.exergen.model.Workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutRepositoryStub implements IWorkoutRepository {

    private final Map<String, Workout> store = new HashMap<>();

    public WorkoutRepositoryStub() {
        // Hardcode some sample workouts here
        Workout w1 = new Workout(
            "w1",
            "Beginner Full Body",
            2,
            List.of("pushups", "squats", "plank"),
            List.of(30, 30, 30),
            List.of(15, 15, 15)
        );

        Workout w2 = new Workout(
            "w2",
            "Upper Body Blast",
            3,
            List.of("pushups", "triceps_dips", "shoulder_taps"),
            List.of(20, 25, 20),
            List.of(10, 10, 10)
        );

        store.put(w1.getId(), w1);
        store.put(w2.getId(), w2);
    }

    @Override
    public void saveWorkout(Workout workout) {
        store.put(workout.getId(), workout);
    }

    @Override
    public Workout getWorkoutById(String workoutId) {
        return store.get(workoutId);
    }

    @Override
    public List<Workout> getAllWorkouts() {
        return new ArrayList<>(store.values());
    }

    public void seedData() {
        // Left empty intentionally, stub automatically seeds data in constructor
    }
}

