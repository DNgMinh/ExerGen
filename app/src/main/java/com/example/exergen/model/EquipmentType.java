package com.example.exergen.model;

public enum EquipmentType {
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell");

    private final String label;

    EquipmentType(String label) {
        this.label = label;
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
