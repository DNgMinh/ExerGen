package com.example.exergen.model;

import java.util.ArrayList;
import java.util.Collections;
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

        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("ID required");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name required");
        if (muscleGroups == null)
            throw new IllegalArgumentException("muscleGroups required");
        if (equipment == null)
            throw new IllegalArgumentException("Equipment required");
        if (intensity < 0)
            throw new IllegalArgumentException("Intensity must be >= 0");
        if (imageName == null || imageName.trim().isEmpty())
            throw new IllegalArgumentException("Image name required");

        this.id = id;
        this.name = name;
        this.muscleGroups = Collections.unmodifiableList(new ArrayList<>(muscleGroups));
        this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
        this.instructions = instructions;
        this.intensity = intensity;
        this.imageName = imageName;
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