package com.example.exergen.persistence.repository;

import com.example.exergen.model.Workout;

import java.util.List;

public interface IWorkoutRepository {
    void saveWorkout(Workout workout);

    Workout getWorkoutById(String workoutId);

    List<Workout> getAllWorkouts();

    void deleteWorkout(String workoutId);

    void seedData();
}
