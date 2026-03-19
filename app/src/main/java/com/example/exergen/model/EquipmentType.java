package com.example.exergen.model;

import java.util.Arrays;
import java.util.List;

public enum EquipmentType {
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells", "dumbbell"),
    BARBELL("Barbell"),
    CABLE("Cable"),
    EZ_CURL_BAR("E-Z Curl Bar"),
    MACHINE("Machine"),
    KETTLEBELL("Kettlebell");

    private final String label;
    private final List<String> aliases;
    EquipmentType(String label) {
        this(label, new String[0]);
    }

    EquipmentType(String label, String... aliases) {
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

    public static EquipmentType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type value cannot be null or empty");
        }

        EquipmentType match = findMatch(value);

        if (match == null) {
            throw new IllegalArgumentException("Cannot map invalid equipment type: '" + value + "'");
        }
        return match;
    }

    private static String normalize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    private static EquipmentType findMatch(String value) {
        String normalizedInput = normalize(value);

        for (EquipmentType type : values()) {
            if (normalize(type.label).equals(normalizedInput) ||
                    normalize(type.name()).equals(normalizedInput)) {
                return type;
            }
            for (String alias : type.aliases) {
                if (normalize(alias).equals(normalizedInput)) {
                    return type;
                }
            }
        }
        return null;
    }
}