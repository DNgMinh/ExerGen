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


    public WorkoutGenerationConstraints(List<String> equipmentLabels,
            List<String> muscleLabels,
            int targetExerciseCount) {

        ValidationHelper.validateEquipment(equipmentLabels);
        ValidationHelper.validateMuscles(muscleLabels);

        this.selectedEquipment = Collections.unmodifiableList(EnumMapper.toEquipmentEnums(equipmentLabels));
        this.targetMuscleGroups = Collections.unmodifiableList(EnumMapper.toMuscleEnums(muscleLabels));

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
        if (exercise == null) {
            return false;
        }
        return matchesMuscleGroup(exercise) && matchesEquipment(exercise);
    }

    private boolean matchesMuscleGroup(Exercise exercise) {
        for (MuscleGroup exerciseMuscle : exercise.getMuscleGroups()) {
            if (targetMuscleGroups.contains(exerciseMuscle)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesEquipment(Exercise exercise) {
        if (selectedEquipment.isEmpty()) {
            return true;
        }

        for (EquipmentType exerciseEquipment : exercise.getEquipment()) {
            if (selectedEquipment.contains(exerciseEquipment)) {
                return true;
            }
        }
        return false;
    }
}