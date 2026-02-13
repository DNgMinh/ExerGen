package com.example.exergen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exercise {

    private final String id; // Unique key (UUID or short string)
    private final String name; // Display name
    private final List<String> muscleGroups; // e.g. ["chest", "triceps"]
    private final List<String> equipment; // e.g. ["dumbbells"] or ["bodyweight"]
    private final String instructions; // Optional text
    private final int intensity; // 1-5 scale (or similar)

    public Exercise(String id,
            String name,
            List<String> muscleGroups,
            List<String> equipment,
            String instructions,
            int intensity) {

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

        this.id = id;
        this.name = name;
        this.muscleGroups = Collections.unmodifiableList(new ArrayList<>(muscleGroups));
        this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
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
}
