package com.example.exergen.model;

public enum EquipmentType {
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbell"),
    BARBELL("Barbell"),
    EZ_CURL_BAR("E-Z Curl Bar"),
    MACHINE("Machine"),
    CABLE("Cable");

    private final String label;

    EquipmentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean isValidLabel(String value) {
        for (EquipmentType type : values()) {
            if (type.label.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
