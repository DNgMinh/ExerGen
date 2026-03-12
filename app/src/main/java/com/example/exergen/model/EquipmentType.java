package com.example.exergen.model;

public enum EquipmentType implements LabeledEnum{

    BODYWEIGHT("Bodyweight"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell"),
    BENCH("Bench"),
    CABLE("Cable");
    private final String label;

    EquipmentType(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }


    public boolean isValidLabel(String value) {
        for (EquipmentType type : values()) {
            if (type.label.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static EquipmentType fromLabel(String value) {
        String normalizedLabel = value.trim();
        for (EquipmentType equipment : EquipmentType.values()) {
            if (equipment.label.equalsIgnoreCase(normalizedLabel)) {
                return equipment;
            }
        }
        throw new IllegalArgumentException("Invalid muscle group label: " + value);
    }
}
