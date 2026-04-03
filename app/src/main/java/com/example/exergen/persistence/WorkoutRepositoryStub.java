package com.example.exergen.persistence;

import com.example.exergen.model.Workout;
import com.example.exergen.persistence.repository.IWorkoutRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutRepositoryStub implements IWorkoutRepository {

    private final Map<String, Workout> store = new HashMap<>();

    public WorkoutRepositoryStub() {
        for (Workout workout : DefaultWorkoutSeedData.createDefaultWorkouts()) {
            store.put(workout.getId(), workout);
        }
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

    @Override
    public void deleteWorkout(String workoutId) {
        if (workoutId == null || workoutId.trim().isEmpty()) {
            return;
        }
        store.remove(workoutId);
    }

    public void seedData() {
        // Left empty intentionally, stub automatically seeds data in constructor
    }
}
