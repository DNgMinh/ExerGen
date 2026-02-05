package com.example.exergen.business.model;

import java.util.List;

public class Exercise {

    private final String id;                 // unique key (UUID or short string)
    private final String name;               // display name
    private final List<String> muscleGroups; // e.g. ["chest", "triceps"]
    private final List<String> equipment;    // e.g. ["dumbbells"] or ["bodyweight"]
    private final String instructions;       // optional text

    public Exercise(String id,
                    String name,
                    List<String> muscleGroups,
                    List<String> equipment,
                    String instructions) {

        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id required");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name required");
        if (muscleGroups == null) throw new IllegalArgumentException("muscleGroups required");
        if (equipment == null) throw new IllegalArgumentException("equipment required");

        this.id = id;
        this.name = name;
        this.muscleGroups = muscleGroups;
        this.equipment = equipment;
        this.instructions = instructions;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public List<String> getMuscleGroups() { return muscleGroups; }

    public List<String> getEquipment() { return equipment; }

    public String getInstructions() { return instructions; }
}

