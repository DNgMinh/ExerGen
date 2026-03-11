package com.example.exergen.model;

public enum EquipmentType implements LabeledEnum{
    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell");

    private final String label;

    EquipmentType(String label) {
        this.label = label;
    }

    @Override
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
