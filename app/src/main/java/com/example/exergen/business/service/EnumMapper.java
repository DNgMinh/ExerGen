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

        return labels.stream()
                .filter(label -> label != null && !label.trim().isEmpty())
                .map(EquipmentType::fromString)
                .collect(Collectors.toList());
    }

    @Override
    public List<MuscleGroup> toMuscleEnums(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return new ArrayList<>();
        }

        return labels.stream()
                .filter(label -> label != null && !label.trim().isEmpty())
                .map(MuscleGroup::fromString)
                .collect(Collectors.toList());
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
