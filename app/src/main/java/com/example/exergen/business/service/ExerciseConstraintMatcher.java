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
        return exercise.matches(selectedEquipment, targetMuscleGroups);
    }
}
