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


    public WorkoutGenerationConstraints(IEnumMapper enumMapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetExerciseCount) {

        ValidationHelper.requireNonNull(enumMapper, "enumMapper required");
        ValidationHelper.validateEquipment(equipmentLabels);
        ValidationHelper.validateMuscles(muscleLabels);

        this.selectedEquipment = Collections.unmodifiableList(enumMapper.toEquipmentEnums(equipmentLabels));
        this.targetMuscleGroups = Collections.unmodifiableList(enumMapper.toMuscleEnums(muscleLabels));

        if (targetExerciseCount <= 0) {
            throw new IllegalArgumentException("targetExerciseCount must be > 0");
        }
        this.targetExerciseCount = targetExerciseCount;
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

    public boolean matchesExercise(Exercise exercise) {
        return ExerciseConstraintMatcher.matches(exercise, selectedEquipment, targetMuscleGroups);
    }
}
