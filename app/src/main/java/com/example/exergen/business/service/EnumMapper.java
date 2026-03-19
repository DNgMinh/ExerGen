package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class EnumMapper {
    public EnumMapper() {}

    public static List<EquipmentType> toEquipmentEnums(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }

        return labels.stream()
                .filter(label -> label != null && !label.trim().isEmpty())
                .map(EquipmentType::fromString)
                .collect(Collectors.toList());
    }

    public static List<MuscleGroup> toMuscleEnums(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }

        return labels.stream()
                .filter(label -> label != null && !label.trim().isEmpty())
                .map(MuscleGroup::fromString)
                .collect(Collectors.toList());
    }
}