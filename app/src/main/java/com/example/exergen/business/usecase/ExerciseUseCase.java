package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.EquipmentType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseUseCase {
    private final ExerciseService exerciseService;

    // List of filters for exercise equipment
    private List<EquipmentType> currentFilters = new ArrayList<>(Arrays.asList(EquipmentType.values()));

    public ExerciseUseCase(ExerciseService exerciseService) {
        if (exerciseService == null) {
            throw new IllegalArgumentException("exerciseService required");
        }
        this.exerciseService = exerciseService;
    }

    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    public Exercise getExerciseById(String id) {
        return exerciseService.getExerciseById(id);
    }

    public void setEquipmentFilters(List<EquipmentType> filters) {
        this.currentFilters = new ArrayList<>(filters);
    }

    public List<EquipmentType> getEquipmentFilters() {
        return new ArrayList<>(currentFilters);
    }

    public List<Exercise> getFilteredExercises() {
        return getExercisesByEquipment(currentFilters);
    }

    public List<Exercise> getExercisesByEquipment(List<EquipmentType> selectedEquipment) {
        List<Exercise> allExercises = exerciseService.getAllExercises();

        if (selectedEquipment == null || selectedEquipment.isEmpty()) {
            return new ArrayList<>();
        }

        List<Exercise> filtered = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            // Check if ANY piece of equipment required for the exercise is in our selected list
            for (EquipmentType e : exercise.getEquipment()) {
                if (selectedEquipment.contains(e)) {
                    filtered.add(exercise);
                    break;
                }
            }
        }
        return filtered;
    }
}
