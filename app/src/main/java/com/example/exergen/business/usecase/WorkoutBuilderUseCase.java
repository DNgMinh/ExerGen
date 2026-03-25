package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WorkoutBuilderUseCase {
    private static final int DEFAULT_WORK_SECONDS = 45;
    private static final int DEFAULT_REST_SECONDS = 15;
    private static final DateTimeFormatter GENERATED_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ExerciseService exerciseService;
    private final IEnumMapper enumMapper;

    public WorkoutBuilderUseCase(ExerciseService exerciseService, IEnumMapper enumMapper) {
        if (exerciseService == null) {
            throw new IllegalArgumentException("exerciseService required.");
        }
        if (enumMapper == null) {
            throw new IllegalArgumentException("enumMapper required.");
        }
        this.exerciseService = exerciseService;
        this.enumMapper = enumMapper;
    }

    public Workout generateWorkout(WorkoutGenerationConstraints constraints) {
        if (constraints == null) {
            throw new IllegalArgumentException("constraints required.");
        }

        List<Exercise> matchingExercises = exerciseService.filterByConstraints(
                constraints.getSelectedEquipment(),
                constraints.getTargetMuscleGroups()
        );

        if (matchingExercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises match the selected constraints.");
        }

        // Shuffle to avoid always picking the same exercises in the same order
        List<Exercise> shuffled = new ArrayList<>(matchingExercises);
        Collections.shuffle(shuffled);

        int exerciseCount = constraints.getTargetExerciseCount();

        List<WorkoutStep> steps = new ArrayList<>();

        for (int i = 0; i < exerciseCount; i++) {
            // Cycle through matching exercises if we need more than available
            Exercise selected = shuffled.get(i % shuffled.size());
            steps.add(new WorkoutStep(selected.getId(), DEFAULT_WORK_SECONDS, DEFAULT_REST_SECONDS));
        }

        String generatedId = "generated-" + UUID.randomUUID();
        return new Workout(
                generatedId,
                createGeneratedWorkoutName(),
                1,
                steps);
    }

    public Workout generateWorkout(
            List<String> selectedEquipmentLabels,
            List<String> targetMuscleLabels,
            int targetExerciseCount) {
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(
                enumMapper,
                selectedEquipmentLabels,
                targetMuscleLabels,
                targetExerciseCount);
        return generateWorkout(constraints);
    }

    private String createGeneratedWorkoutName() {
        return "Generated Workout - " + LocalDateTime.now().format(GENERATED_NAME_FORMATTER);
    }
}
