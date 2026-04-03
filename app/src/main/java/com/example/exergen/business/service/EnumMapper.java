package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnumMapper implements IEnumMapper {

    public EnumMapper() {}

    @Override
    public List<EquipmentType> toEquipmentEnums(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }

        List<EquipmentType> results = new ArrayList<>();
        for (String label : labels) {
            if (label == null || label.trim().isEmpty()) continue;
            try {
                results.add(EquipmentType.fromString(label));
            } catch (IllegalArgumentException e) {
                // Skipping unknown equipment. Logging is omitted to keep business logic independent of Android.
            }
        }
        return results;
    }

    @Override
    public List<MuscleGroup> toMuscleEnums(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }

        List<MuscleGroup> results = new ArrayList<>();
        for (String label : labels) {
            if (label == null || label.trim().isEmpty()) continue;
            try {
                results.add(MuscleGroup.fromString(label));
            } catch (IllegalArgumentException e) {
                // Skipping unknown muscle group. Logging is omitted to keep business logic independent of Android.
            }
        }
        return results;
    }

    @Override
    public List<String> toEquipmentLabels(List<EquipmentType> types) {
        if (types == null || types.isEmpty()) {
            return new ArrayList<>();
        }
        return types.stream()
                .map(EquipmentType::getLabel)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> toMuscleLabels(List<MuscleGroup> groups) {
        if (groups == null || groups.isEmpty()) {
            return new ArrayList<>();
        }
        return groups.stream()
                .map(MuscleGroup::getLabel)
                .collect(Collectors.toList());
    }
}
