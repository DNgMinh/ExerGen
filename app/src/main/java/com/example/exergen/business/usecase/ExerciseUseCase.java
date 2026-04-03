package com.example.exergen.business.usecase;

import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseUseCase {
    private final ExerciseService exerciseService;

    // List of filters for exercise equipment and muscle groups
    private List<EquipmentType> equipmentFilters = new ArrayList<>(Arrays.asList(EquipmentType.values()));
    private List<MuscleGroup> muscleFilters = new ArrayList<>(Arrays.asList(MuscleGroup.values()));

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
        this.equipmentFilters = new ArrayList<>(filters);
    }

    public List<EquipmentType> getEquipmentFilters() {
        return new ArrayList<>(equipmentFilters);
    }

    public void setMuscleFilters(List<MuscleGroup> filters) {
        this.muscleFilters = new ArrayList<>(filters);
    }

    public List<MuscleGroup> getMuscleFilters() {
        return new ArrayList<>(muscleFilters);
    }

    public List<Exercise> getFilteredExercises() {
        List<Exercise> allExercises = exerciseService.getAllExercises();
        List<Exercise> filtered = new ArrayList<>();

        for (Exercise exercise : allExercises) {
            boolean matchesEquipment = false;
            for (EquipmentType e : exercise.getEquipment()) {
                if (equipmentFilters.contains(e)) {
                    matchesEquipment = true;
                    break;
                }
            }

            boolean matchesMuscle = false;
            for (MuscleGroup m : exercise.getMuscleGroups()) {
                if (muscleFilters.contains(m)) {
                    matchesMuscle = true;
                    break;
                }
            }

            if (matchesEquipment && matchesMuscle) {
                filtered.add(exercise);
            }
        }
        return filtered;
    }
}
