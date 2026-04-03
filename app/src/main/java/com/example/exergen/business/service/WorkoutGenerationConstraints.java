package com.example.exergen.business.service;

import com.example.exergen.business.validation.ValidationHelper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates validation and matching rules for Workout Builder generation inputs.
 */
public final class WorkoutGenerationConstraints {
    private final List<EquipmentType> selectedEquipment;
    private final List<MuscleGroup> targetMuscleGroups;
    private final int targetExerciseCount;
    private final int targetDurationSeconds;
    private final int workSeconds;
    private final int restSeconds;
    private final boolean isTimeBased;

    /**
     * Shorthand constructor for Exercise Count based generation with default intervals.
     */
    public WorkoutGenerationConstraints(IEnumMapper enumMapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetExerciseCount) {
        this(enumMapper, equipmentLabels, muscleLabels, targetExerciseCount, 0, 45, 15, false);
    }

    /**
     * Static factory for Exercise Count based generation with custom intervals.
     */
    public static WorkoutGenerationConstraints createCountBased(IEnumMapper enumMapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetExerciseCount,
            int workSeconds,
            int restSeconds) {
        return new WorkoutGenerationConstraints(enumMapper, equipmentLabels, muscleLabels,
                targetExerciseCount, 0, workSeconds, restSeconds, false);
    }

    /**
     * Static factory for Time based generation.
     */
    public static WorkoutGenerationConstraints createTimeBased(IEnumMapper enumMapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetDurationSeconds,
            int workSeconds,
            int restSeconds) {
        return new WorkoutGenerationConstraints(enumMapper, equipmentLabels, muscleLabels,
                0, targetDurationSeconds, workSeconds, restSeconds, true);
    }

    private WorkoutGenerationConstraints(IEnumMapper enumMapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetExerciseCount,
            int targetDurationSeconds,
            int workSeconds,
            int restSeconds,
            boolean isTimeBased) {

        ValidationHelper.requireNonNull(enumMapper, "enumMapper required");
        ValidationHelper.validateEquipment(equipmentLabels);
        ValidationHelper.validateMuscles(muscleLabels);

        this.selectedEquipment = Collections.unmodifiableList(enumMapper.toEquipmentEnums(equipmentLabels));
        this.targetMuscleGroups = Collections.unmodifiableList(enumMapper.toMuscleEnums(muscleLabels));
        this.isTimeBased = isTimeBased;
        this.workSeconds = workSeconds;
        this.restSeconds = restSeconds;

        if (isTimeBased) {
            if (targetDurationSeconds <= 0) {
                throw new IllegalArgumentException("targetDurationSeconds must be > 0");
            }
            this.targetDurationSeconds = targetDurationSeconds;
            this.targetExerciseCount = 0;
        } else {
            if (targetExerciseCount <= 0) {
                throw new IllegalArgumentException("targetExerciseCount must be > 0");
            }
            this.targetExerciseCount = targetExerciseCount;
            this.targetDurationSeconds = 0;
        }

        if (workSeconds <= 0) {
            throw new IllegalArgumentException("workSeconds must be > 0");
        }
        if (restSeconds < 0) {
            throw new IllegalArgumentException("restSeconds must be >= 0");
        }
    }

    public List<EquipmentType> getSelectedEquipment() {
        return selectedEquipment;
    }

    public List<MuscleGroup> getTargetMuscleGroups() {
        return targetMuscleGroups;
    }

    public int getTargetExerciseCount() {
        return targetExerciseCount;
    }

    public int getTargetDurationSeconds() {
        return targetDurationSeconds;
    }

    public int getWorkSeconds() {
        return workSeconds;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    public boolean isTimeBased() {
        return isTimeBased;
    }

    public boolean matchesExercise(Exercise exercise) {
        return ExerciseConstraintMatcher.matches(exercise, selectedEquipment, targetMuscleGroups);
    }
}
