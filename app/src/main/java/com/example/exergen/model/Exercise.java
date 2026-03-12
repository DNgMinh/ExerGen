package com.example.exergen.model;

import com.example.exergen.business.validation.ValidationHelper;

import java.util.List;

public class Exercise {

    private final String id; // Unique key (UUID)
    private final String name; // Display name
    private final List<MuscleGroup> muscleGroups; // e.g. ["chest", "triceps"]
    private final List<EquipmentType> equipment; // e.g. ["dumbbells"] or ["bodyweight"]
    private final String instructions; // Instructions
    private final int intensity; // 1-5 scale
    private final List<String> imagePaths; // e.g. ["exercises/planks/0.jpg", "exercises/planks/1.jpg"]

    public Exercise(String id,
            String name,
            List<MuscleGroup> muscleGroups,
            List<EquipmentType> equipment,
            String instructions,
            int intensity,
            List<String> imagePaths) {
        this.id = ModelValidation.requireNonBlank(id, "ID required");
        this.name = ModelValidation.requireNonBlank(name, "Name required");
        ModelValidation.requireNonEmptyList(muscleGroups, "Muscle groups required");
        ModelValidation.requireNonEmptyList(equipment, "Equipment required");
        ModelValidation.requireNonEmptyList(imagePaths, "Image paths required");

        this.id = ValidationHelper.requireNonBlank(id, "ID required");
        this.name = ValidationHelper.requireNonBlank(name, "Name required");
        ValidationHelper.requireNonEmptyList(muscleGroups, "muscleGroups required");
        ValidationHelper.requireNonEmptyList(equipment, "Equipment required");
        if (intensity < 0){
            throw new IllegalArgumentException("Intensity must be >= 0");
            }

        this.muscleGroups = List.copyOf(muscleGroups);
        this.equipment = List.copyOf(equipment);
        this.instructions = instructions;
        this.intensity = intensity;
        this.imagePaths = List.copyOf(imagePaths);
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MuscleGroup> getMuscleGroups() {
        return muscleGroups;
    }

    public List<EquipmentType> getEquipment() {
        return equipment;
    }

    public String getInstructions() {
        return instructions;
    }

    public int getIntensity() {
        return intensity;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }
}