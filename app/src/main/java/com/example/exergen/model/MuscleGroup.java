package com.example.exergen.model;

public enum MuscleGroup implements LabeledEnum{
    CHEST("Chest"),
    TRICEPS("Triceps"),
    BICEPS("Biceps"),
    LEGS("Legs"),
    GLUTES("Glutes"),
    CORE("Core"),
    BACK("Back"),
    ARMS("Arms"),
    SHOULDERS("Shoulders"),
    FULL_BODY("Full Body");

    private final String label;

    MuscleGroup(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }


    public boolean isValidLabel(String value) {
        for (MuscleGroup group : values()) {
            if (group.label.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static MuscleGroup fromLabel(String value) {
        String normalizedLabel = value.trim();
        for (MuscleGroup group : MuscleGroup.values()) {
            if (group.label.equalsIgnoreCase(normalizedLabel)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid muscle group label: " + value);
    }
}
