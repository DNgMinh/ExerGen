package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.ArrayList;
import java.util.List;

public class EnumMapper {
    public EnumMapper() {}

    public List<MuscleGroup> toMuscleEnums(List<String> labels) {
        List<MuscleGroup> results = new ArrayList<>();
        if (labels == null) return results;

        for (String label : labels) {
            if (label != null && !label.trim().isEmpty()) {
                String trimmed = label.trim();
                MuscleGroup match = null;

                for (MuscleGroup group : MuscleGroup.values()) {
                    if (group.getLabel().equalsIgnoreCase(trimmed)) {
                        match = group;
                        break;
                    }
                }

                if (match == null) {
                    throw new IllegalArgumentException("Unknown muscle group: " + label);
                }
                results.add(match);
            }
        }
        return results;
    }

    public List<EquipmentType> toEquipmentEnums(List<String> labels) {
        List<EquipmentType> results = new ArrayList<>();

        if (labels != null) {
            for (String label : labels) {
                if (label != null && !label.trim().isEmpty()) {
                    String trimmed = label.trim();
                    EquipmentType match = null;

                    for (EquipmentType equipment : EquipmentType.values()) {
                        if (equipment.getLabel().equalsIgnoreCase(trimmed) ||
                                equipment.name().equalsIgnoreCase(trimmed) ||
                                (equipment == EquipmentType.DUMBBELLS && (trimmed.equalsIgnoreCase("Dumbbell") || trimmed.equalsIgnoreCase("Dumbbells"))) ||
                                (equipment == EquipmentType.EZ_CURL_BAR && trimmed.equalsIgnoreCase("EZ Curl Bar"))) {
                            match = equipment;
                            break;
                        }
                    }

                    if (match == null) {
                        throw new IllegalArgumentException("Unknown equipment: " + label);
                    }
                    results.add(match);
                }
            }
        }

        if (results.isEmpty()) {
            results.add(EquipmentType.BODYWEIGHT);
        }

        return results;
    }
}