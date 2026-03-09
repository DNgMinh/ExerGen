package com.example.exergen.business.service;

import com.example.exergen.model.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates validation and matching rules for Workout Builder generation
 * inputs.
 */
public final class WorkoutGenerationConstraints {
    private final List<String> selectedEquipment;
    private final List<String> targetMuscleGroups;
    private final int desiredDurationMinutes;

    public WorkoutGenerationConstraints(List<String> selectedEquipment,
            List<String> targetMuscleGroups,
            int desiredDurationMinutes) {
        this.selectedEquipment = normalizeListAllowEmpty(selectedEquipment, "selectedEquipment required");
        this.targetMuscleGroups = normalizeListRequireAtLeastOne(targetMuscleGroups, "targetMuscleGroups required");

        if (desiredDurationMinutes <= 0) {
            throw new IllegalArgumentException("desiredDurationMinutes must be > 0");
        }
        this.desiredDurationMinutes = desiredDurationMinutes;
    }

    public List<String> getSelectedEquipment() {
        return selectedEquipment;
    }

    public List<String> getTargetMuscleGroups() {
        return targetMuscleGroups;
    }

    public int getDesiredDurationMinutes() {
        return desiredDurationMinutes;
    }

    public boolean matchesExercise(Exercise exercise) {
        if (exercise == null) {
            return false;
        }
        return matchesMuscleGroup(exercise) && matchesEquipment(exercise);
    }

    private boolean matchesMuscleGroup(Exercise exercise) {
        for (String target : targetMuscleGroups) {
            for (String exerciseMuscle : exercise.getMuscleGroups()) {
                if (target.equalsIgnoreCase(exerciseMuscle)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesEquipment(Exercise exercise) {
        if (selectedEquipment.isEmpty()) {
            return true;
        }

        for (String selected : selectedEquipment) {
            for (String exerciseEquipment : exercise.getEquipment()) {
                if (selected.equalsIgnoreCase(exerciseEquipment)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<String> normalizeListAllowEmpty(List<String> values, String nullMessage) {
        if (values == null) {
            throw new IllegalArgumentException(nullMessage);
        }
        return Collections.unmodifiableList(normalizeEntries(values));
    }

    private static List<String> normalizeListRequireAtLeastOne(List<String> values, String nullMessage) {
        if (values == null) {
            throw new IllegalArgumentException(nullMessage);
        }
        List<String> normalized = normalizeEntries(values);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("targetMuscleGroups must contain at least one value");
        }
        return Collections.unmodifiableList(normalized);
    }

    private static List<String> normalizeEntries(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Constraint values must be non-blank.");
            }
            result.add(value.trim());
        }
        return result;
    }
}
