package com.example.exergen.business.validation;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.List;

public class ConstraintValidator {
    public ConstraintValidator() {}
    public void validateMuscles(List<String> labels) {
        validateBasicList(labels, "targetMuscleGroups");

        if (labels.isEmpty()) {
            throw new IllegalArgumentException("targetMuscleGroups must contain at least one value");
        }

        validateLabels(labels, "MuscleGroup", MuscleGroup.values());
    }

    // Check Nulls and Blank Strings
    public void validateEquipment(List<String> labels) {
        validateBasicList(labels, "selectedEquipment");
        validateLabels(labels, "EquipmentType", EquipmentType::isValidLabel);
    }

    // PRIVATE  HELPERS
    private void validateBasicList(List<String> labels, String fieldName) {
        if (labels == null) {
            throw new IllegalArgumentException(fieldName + " list cannot be null");
        }
        for (String label : labels) {
            if (label == null || label.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " entries cannot be blank");
            }
        }
    }

    private void validateLabels(List<String> labels, String typeName, List<String> validOptions) {
        for (String label : labels) {
            if (!validOptions.contains(label.trim())) {
                throw new IllegalArgumentException("Invalid " + typeName + ": " + label);
            }
        }
    }
}
