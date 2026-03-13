package com.example.exergen.model;

public enum EquipmentType {
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell"),
    CABLE("Cable"),
    EZ_CURL_BAR("E-Z Curl Bar"),
    MACHINE("Machine"),
    KETTLEBELL("Kettlebell");

    private final String label;

    EquipmentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static EquipmentType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type value cannot be null or empty");
        }

        String cleanValue = value.trim();

        for (EquipmentType type : values()) {
            if (type.label.equalsIgnoreCase(cleanValue) ||
                    type.name().equalsIgnoreCase(cleanValue) ||
                    (type == DUMBBELLS && (cleanValue.equalsIgnoreCase("Dumbbell") || cleanValue.equalsIgnoreCase("Dumbbells")))) {
                return type;
            }
        }
        throw new IllegalArgumentException("Cannot map invalid equipment type: " + cleanValue);
    }

    public static boolean isValidLabel(String value) {
        try {
            fromString(value);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}