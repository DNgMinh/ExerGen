package com.example.exergen.business.service;

import com.example.exergen.business.validation.ConstraintValidator;
import com.example.exergen.model.EnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates validation and matching rules for Workout Builder generation
 * inputs.
 */
public final class WorkoutGenerationConstraints {
    private final List<EquipmentType> selectedEquipment;
    private final List<MuscleGroup> targetMuscleGroups;
    private final int desiredDurationMinutes;

    public WorkoutGenerationConstraints(List<String> equipmentLabels,
                                        List<String> muscleLabels,
                                        int desiredDurationMinutes) {
        this(new ConstraintValidator(), new EnumMapper(), equipmentLabels, muscleLabels, desiredDurationMinutes);
    }
    public WorkoutGenerationConstraints(
            ConstraintValidator validator,
            EnumMapper mapper,
            List<String> equipmentLabels,
            List<String> muscleLabels,
            int desiredDurationMinutes) {
        validator.validateMuscles(muscleLabels);
        validator.validateEquipment(equipmentLabels);

        this.selectedEquipment = Collections.unmodifiableList(mapper.toEquipmentEnums(equipmentLabels));
        this.targetMuscleGroups = Collections.unmodifiableList(mapper.toMuscleEnums(muscleLabels));

        if (desiredDurationMinutes <= 0) {
            throw new IllegalArgumentException("desiredDurationMinutes must be > 0");
        }
        this.desiredDurationMinutes = desiredDurationMinutes;
    }

    public List<EquipmentType> getSelectedEquipment() {
        return selectedEquipment;
    }

    public List<MuscleGroup> getTargetMuscleGroups() {
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
