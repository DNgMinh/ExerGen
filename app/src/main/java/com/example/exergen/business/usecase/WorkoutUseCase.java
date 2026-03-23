package com.example.exergen.business.usecase;

import com.example.exergen.persistence.repository.IWorkoutRepository;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.ArrayList;
import java.util.List;

public class WorkoutUseCase {
    private final IWorkoutRepository workoutRepository;
    private final ExerciseService exerciseService;

    public WorkoutUseCase(IWorkoutRepository workoutRepository, ExerciseService exerciseService) {
        this.workoutRepository = workoutRepository;
        this.exerciseService = exerciseService;
    }

    public List<Workout> getAllWorkouts() {
        return workoutRepository.getAllWorkouts();
    }

    public Workout getWorkoutById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return workoutRepository.getWorkoutById(id);
    }

    public void saveWorkout(Workout workout) {
        if (workout == null) {
            throw new IllegalArgumentException("Cannot save a null workout");
        }
        workoutRepository.saveWorkout(workout);
    }

    public void deleteWorkout(String workoutId) {
        if (workoutId == null || workoutId.trim().isEmpty()) {
            throw new IllegalArgumentException("workoutId required");
        }
        workoutRepository.deleteWorkout(workoutId);
    }

    public List<Exercise> getExercisesForWorkout(Workout workout) {
        List<Exercise> exercises = new ArrayList<>();
        if (workout != null && workout.getSteps() != null) {
            for (WorkoutStep step : workout.getSteps()) {
                String id = step.getExerciseId();
                Exercise exercise = exerciseService.getExerciseById(id);
                if (exercise != null) {
                    exercises.add(exercise);
                }
            }
        }
        return exercises;
    }
}