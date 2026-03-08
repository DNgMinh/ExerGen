package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkoutBuilderUseCase {
    private static final int DEFAULT_WORK_SECONDS = 45;
    private static final int DEFAULT_REST_SECONDS = 15;
    private static final int SECONDS_PER_MINUTE = 60;

    private final ExerciseService exerciseService;

    public WorkoutBuilderUseCase(ExerciseService exerciseService) {
        if (exerciseService == null) {
            throw new IllegalArgumentException("exerciseService required.");
        }
        this.exerciseService = exerciseService;
    }

    public Workout generateWorkout(WorkoutGenerationConstraints constraints) {
        if (constraints == null) {
            throw new IllegalArgumentException("constraints required.");
        }

        List<Exercise> matchingExercises = new ArrayList<>();
        for (Exercise exercise : exerciseService.getAllExercises()) {
            if (constraints.matchesExercise(exercise)) {
                matchingExercises.add(exercise);
            }
        }

        if (matchingExercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises match the selected constraints.");
        }

        int targetSeconds = constraints.getDesiredDurationMinutes() * SECONDS_PER_MINUTE;
        int slotSeconds = DEFAULT_WORK_SECONDS + DEFAULT_REST_SECONDS;
        int exerciseCount = Math.max(1, targetSeconds / slotSeconds);

        List<String> exerciseIds = new ArrayList<>();
        List<Integer> workSeconds = new ArrayList<>();
        List<Integer> restSeconds = new ArrayList<>();

        for (int i = 0; i < exerciseCount; i++) {
            Exercise selected = matchingExercises.get(i % matchingExercises.size());
            exerciseIds.add(selected.getId());
            workSeconds.add(DEFAULT_WORK_SECONDS);
            restSeconds.add(DEFAULT_REST_SECONDS);
        }

        String generatedId = "generated-" + UUID.randomUUID();
        return new Workout(
                generatedId,
                "Generated Workout",
                1,
                exerciseIds,
                workSeconds,
                restSeconds);
    }
}
