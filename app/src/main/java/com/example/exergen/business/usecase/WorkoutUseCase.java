package com.example.exergen.business.usecase;

import com.example.exergen.business.model.Workout;
import com.example.exergen.persistence.repository.WorkoutRepository;

import java.util.List;

public class WorkoutUseCase {

    private final WorkoutRepository workoutRepository;

    public WorkoutUseCase(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public void saveWorkout(Workout workout) {
        if (workout == null)
            throw new IllegalArgumentException("workout required");
        workoutRepository.saveWorkout(workout);
    }

    public Workout loadWorkout(String id) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("id required");
        return workoutRepository.getWorkoutById(id);
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository.getAllWorkouts();
    }
}
