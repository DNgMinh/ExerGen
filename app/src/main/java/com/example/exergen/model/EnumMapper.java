package com.example.exergen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumMapper {
    public EnumMapper() {}

    public List<MuscleGroup> toMuscleEnums(List<String> labels) {
        List<MuscleGroup> results = new ArrayList<>();

        if (labels == null) {
            return results;
        }

        for (String label : labels) {
            if (label != null && !label.trim().isEmpty()) {
                boolean matchFound = false;

                for (MuscleGroup group : MuscleGroup.values()) {
                    if (group.getLabel().equalsIgnoreCase(label.trim())) {
                        results.add(group);
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    throw new IllegalArgumentException("Cannot map invalid muscle group: '" + label + "'");
                }
            }
        }

        return results;
    }


    public List<EquipmentType> toEquipmentEnums(List<String> labels) {
        List<EquipmentType> results = new ArrayList<>();

        if (labels == null) {
            return results;
        }

        for (String label : labels) {
            if (label != null && !label.trim().isEmpty()) {
                boolean matchFound = false;

                for (EquipmentType equipment : EquipmentType.values()) {
                    if (equipment.getLabel().equalsIgnoreCase(label.trim())) {
                        results.add(equipment);
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    throw new IllegalArgumentException("Cannot map invalid equipment type: '" + label + "'");
                }
            }
        }

        return results;
    }
}
