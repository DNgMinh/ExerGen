package com.example.exergen.model;

import java.util.List;

public class Exercise {

    private final String id; // Unique key (UUID)
    private final String name; // Display name
    private final List<String> muscleGroups; // e.g. ["chest", "triceps"]
    private final List<String> equipment; // e.g. ["dumbbells"] or ["bodyweight"]
    private final String instructions; // Instructions
    private final int intensity; // 1-5 scale
    private final String imageName; // Corresponding image in /res/drawable

    public Exercise(String id,
            String name,
            List<String> muscleGroups,
            List<String> equipment,
            String instructions,
            int intensity,
            String imageName) {
        this.id = ModelValidation.requireNonBlank(id, "ID required");
        this.name = ModelValidation.requireNonBlank(name, "Name required");
        ModelValidation.requireNonEmptyList(muscleGroups, "muscleGroups required");
        ModelValidation.requireNonEmptyList(equipment, "Equipment required");
        if (intensity < 0)
            throw new IllegalArgumentException("Intensity must be >= 0");
        this.imageName = ModelValidation.requireNonBlank(imageName, "Image name required");

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

    public List<String> getMuscleGroups() {
        return muscleGroups;
    }

    public List<String> getEquipment() {
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