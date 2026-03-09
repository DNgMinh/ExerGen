package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

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
        List<String> exerciseIds = workout.getExerciseIds();
        for (int i = 0; i < exerciseIds.size(); i++) {
            String id = exerciseIds.get(i);
            Exercise exercise = exerciseService.getExerciseById(id);
            String exerciseName = exercise != null ? exercise.getName() : id;
            items.add(new WorkoutPreviewItem(
                    i + 1,
                    exerciseName,
                    workout.getWorkSeconds().get(i),
                    workout.getRestSeconds().get(i)));
        }

        return new WorkoutPreviewData(items);
    }
}
