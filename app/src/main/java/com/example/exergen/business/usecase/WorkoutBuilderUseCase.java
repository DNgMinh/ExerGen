package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.IEnumMapper;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.example.exergen.model.Workout;
import com.example.exergen.model.WorkoutStep;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WorkoutBuilderUseCase {
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

        int exerciseCount;
        int rounds = 1;
        int workSecs = constraints.getWorkSeconds();
        int restSecs = constraints.getRestSeconds();

        if (constraints.isTimeBased()) {
            int stepDuration = workSecs + restSecs;
            int totalSlots = constraints.getTargetDurationSeconds() / stepDuration;

            if (totalSlots <= 0) {
                throw new IllegalArgumentException("Target duration is too short for these intervals.");
            }

            // Logical balancing of rounds vs exercises
            if (totalSlots >= 12) {
                rounds = 3;
                exerciseCount = totalSlots / 3;
            } else if (totalSlots >= 6) {
                rounds = 2;
                exerciseCount = totalSlots / 2;
            } else {
                rounds = 1;
                exerciseCount = totalSlots;
            }
        } else {
            exerciseCount = constraints.getTargetExerciseCount();
        }

        List<WorkoutStep> steps = selectBalancedExercises(matchingExercises, constraints.getTargetMuscleGroups(), exerciseCount, workSecs, restSecs);

        String generatedId = "generated-" + UUID.randomUUID();
        return new Workout(
                generatedId,
                createGeneratedWorkoutName(),
                rounds,
                steps);
    }

    private List<WorkoutStep> selectBalancedExercises(
            List<Exercise> allMatching,
            List<MuscleGroup> targetMuscles,
            int count,
            int work,
            int rest) {

        // 1. Group into buckets
        Map<MuscleGroup, List<Exercise>> buckets = new HashMap<>();
        for (MuscleGroup mg : targetMuscles) {
            buckets.put(mg, new ArrayList<>());
        }

        for (Exercise ex : allMatching) {
            for (MuscleGroup mg : ex.getMuscleGroups()) {
                if (buckets.containsKey(mg)) {
                    buckets.get(mg).add(ex);
                }
            }
        }

        // Shuffle each bucket
        for (List<Exercise> bucket : buckets.values()) {
            Collections.shuffle(bucket);
        }

        List<WorkoutStep> selectedSteps = new ArrayList<>();
        List<MuscleGroup> roundRobinQueue = new ArrayList<>(targetMuscles);
        Map<MuscleGroup, Integer> bucketIndices = new HashMap<>();
        for (MuscleGroup mg : targetMuscles) {
            bucketIndices.put(mg, 0);
        }

        Set<String> pickedIds = new HashSet<>();
        int totalUniqueAvailable = allMatching.size();

        // 2. Round Robin selection
        int picked = 0;
        int queueIdx = 0;
        while (picked < count) {
            MuscleGroup currentMg = roundRobinQueue.get(queueIdx % roundRobinQueue.size());
            List<Exercise> bucket = buckets.get(currentMg);

            if (bucket != null && !bucket.isEmpty()) {
                Exercise toAdd = null;

                // Try to find a non-picked exercise in this bucket
                if (pickedIds.size() < totalUniqueAvailable) {
                    for (int i = 0; i < bucket.size(); i++) {
                        int idx = (bucketIndices.get(currentMg) + i) % bucket.size();
                        Exercise candidate = bucket.get(idx);
                        if (!pickedIds.contains(candidate.getId())) {
                            toAdd = candidate;
                            bucketIndices.put(currentMg, (idx + 1) % bucket.size());
                            break;
                        }
                    }
                }

                // Fallback: if we must repeat (requested count > unique matches)
                if (toAdd == null) {
                    int idx = bucketIndices.get(currentMg);
                    toAdd = bucket.get(idx % bucket.size());
                    bucketIndices.put(currentMg, (idx + 1) % bucket.size());
                }

                selectedSteps.add(new WorkoutStep(toAdd.getId(), work, rest));
                pickedIds.add(toAdd.getId());
                picked++;
            }
            queueIdx++;
            
            // Safety break
            if (queueIdx > count * 100) break;
        }

        return selectedSteps;
    }

    public Workout generateWorkout(
            List<String> selectedEquipmentLabels,
            List<String> targetMuscleLabels,
            int targetExerciseCount) {
        WorkoutGenerationConstraints constraints = WorkoutGenerationConstraints.createCountBased(
                enumMapper,
                selectedEquipmentLabels,
                targetMuscleLabels,
                targetExerciseCount,
                45, 15);
        return generateWorkout(constraints);
    }

    private String createGeneratedWorkoutName() {
        return "Generated Workout - " + LocalDateTime.now().format(GENERATED_NAME_FORMATTER);
    }
}
