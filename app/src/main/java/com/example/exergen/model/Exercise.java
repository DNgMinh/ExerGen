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
    private final String imageName; // Corresponding image in /res/drawable

    public Exercise(String id,
            String name,
            List<MuscleGroup> muscleGroups,
            List<EquipmentType> equipment,
            String instructions,
            int intensity,
            String imageName) {
        this.id = ValidationHelper.requireNonBlank(id, "ID required");
        this.name = ValidationHelper.requireNonBlank(name, "Name required");
        ValidationHelper.requireNonEmptyList(muscleGroups, "muscleGroups required");
        ValidationHelper.requireNonEmptyList(equipment, "Equipment required");
        if (intensity < 0){
            throw new IllegalArgumentException("Intensity must be >= 0");
            }
        this.imageName = ValidationHelper.requireNonBlank(imageName, "Image name required");

        this.muscleGroups = List.copyOf(muscleGroups);
        this.equipment = List.copyOf(equipment);
        this.instructions = instructions;
        this.intensity = intensity;
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

    public String getImageName() {
        return imageName;
    }
}