package com.example.exergen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumMapper {
    public EnumMapper() {}

    public List<MuscleGroup> toMuscleEnums(List<String> labels) {
        return mapList(MuscleGroup.class, labels);
    }

    public List<EquipmentType> toEquipmentEnums(List<String> labels) {
        return mapList(EquipmentType.class, labels);
    }

    private <T extends Enum<T> & LabeledEnum> T findByLabel(Class<T> enumClass, String label) {
        for (T constant : Objects.requireNonNull(enumClass.getEnumConstants(), "Enum constants cannot be null")) {
            if (constant.getLabel().equalsIgnoreCase(label.trim())) {
                return constant;
            }
        }
        return null;
    }

    private <T extends Enum<T> & LabeledEnum> List<T> mapList(Class<T> enumClass, List<String> labels) {
        List<T> results = new ArrayList<>();
        if (labels != null) {
            for (String label : labels) {
                T matched = findByLabel(enumClass, label);
                if (matched != null) {
                    results.add(matched);
                }
            }
        }
        return results;
    }
}
