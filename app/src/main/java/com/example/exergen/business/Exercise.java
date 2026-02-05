package com.example.exergen.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exercise {
    private final String id;
    private final String name;
    private final String instructions;
    private final List<String> equipment;
    private final List<String> muscleGroups;
    private final int durationSeconds;
    private final int intensity;

    public Exercise(
            String id,
            String name,
            String instructions,
            List<String> equipment,
            List<String> muscleGroups,
            int durationSeconds,
            int intensity) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
        this.muscleGroups = Collections.unmodifiableList(new ArrayList<>(muscleGroups));
        this.durationSeconds = durationSeconds;
        this.intensity = intensity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public List<String> getMuscleGroups() {
        return muscleGroups;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getIntensity() {
        return intensity;
    }
}
