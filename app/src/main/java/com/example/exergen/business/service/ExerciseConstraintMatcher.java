package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.List;

/**
 * Centralized matching rules for exercise constraint filtering.
 */
public final class ExerciseConstraintMatcher {
    private ExerciseConstraintMatcher() {
    }

    public static boolean matches(
            Exercise exercise,
            List<EquipmentType> selectedEquipment,
            List<MuscleGroup> targetMuscleGroups) {
        if (exercise == null) {
            return false;
        }
        return matchesMuscleGroup(exercise, targetMuscleGroups)
                && matchesEquipment(exercise, selectedEquipment);
    }

    private static boolean matchesMuscleGroup(Exercise exercise, List<MuscleGroup> targetMuscleGroups) {
        if (targetMuscleGroups == null || targetMuscleGroups.isEmpty()) {
            return false;
        }
        for (MuscleGroup exerciseMuscle : exercise.getMuscleGroups()) {
            if (targetMuscleGroups.contains(exerciseMuscle)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesEquipment(Exercise exercise, List<EquipmentType> selectedEquipment) {
        if (selectedEquipment == null || selectedEquipment.isEmpty()) {
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
