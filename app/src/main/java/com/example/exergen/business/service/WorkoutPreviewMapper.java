package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPreviewMapper {
    public WorkoutPreviewData map(Workout workout, ExerciseService exerciseService) {
        if (workout == null) {
            throw new IllegalArgumentException("workout required.");
        }
        if (exerciseService == null) {
            throw new IllegalArgumentException("exerciseService required.");
        }

        List<WorkoutPreviewItem> items = new ArrayList<>();
        List<WorkoutStep> steps = workout.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            WorkoutStep step = steps.get(i);
            String id = step.getExerciseId();
            Exercise exercise = exerciseService.getExerciseById(id);
            String exerciseName = exercise != null ? exercise.getName() : id;
            items.add(new WorkoutPreviewItem(
                    i + 1,
                    exerciseName,
                    step.getWorkSeconds(),
                    step.getRestSeconds()));
        }

        return new WorkoutPreviewData(items);
    }
}
