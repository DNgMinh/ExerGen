package com.example.exergen.model;

public enum MuscleGroup {
    CHEST("Chest"),
    TRICEPS("Triceps"),
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

    public static boolean isValidLabel(String value) {
        for (MuscleGroup group : values()) {
            if (group.label.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
