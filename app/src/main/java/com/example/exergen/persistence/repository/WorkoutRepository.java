package com.example.exergen.persistence.repository;

import com.example.exergen.business.model.Workout;

import java.util.List;

public interface WorkoutRepository {
    void saveWorkout(Workout workout);

    Workout getWorkoutById(String workoutId);

    List<Workout> getAllWorkouts();
}
