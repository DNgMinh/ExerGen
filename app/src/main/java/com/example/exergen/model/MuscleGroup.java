package com.example.exergen.model;

import java.util.Arrays;
import java.util.List;

public enum MuscleGroup{
    CHEST("Chest"),
    TRICEPS("Triceps"),
    BICEPS("Biceps"),
    LEGS("Legs"),
    GLUTES("Glutes"),
    CORE("Core"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    FULL_BODY("Full Body");

    private final String label;
    private final List<String> aliases;
    MuscleGroup(String label) {
        this(label, new String[0]);
    }

    MuscleGroup(String label, String... aliases) {
        this.label = label;
        this.aliases = Arrays.asList(aliases);
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValidLabel(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return findMatch(value) != null;
    }

    public static MuscleGroup fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Muscle group value cannot be null or empty");
        }

        MuscleGroup match = findMatch(value);

        if (match == null) {
            throw new IllegalArgumentException("Cannot map invalid muscle group: '" + value + "'");
        }
        return match;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    private static MuscleGroup findMatch(String value) {
        String normalizedInput = normalize(value);

        for (MuscleGroup group : values()) {
            if (normalize(group.label).equals(normalizedInput) ||
                    normalize(group.name()).equals(normalizedInput)) {
                return group;
            }
            for (String alias : group.aliases) {
                if (normalize(alias).equals(normalizedInput)) {
                    return group;
                }
            }
        }
        return null;
    }
}
