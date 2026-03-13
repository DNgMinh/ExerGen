package com.example.exergen.business.validation;

import com.example.exergen.business.exception.InvalidTimerConfigurationException;
import java.util.List;

public final class ValidationHelper {
    private ValidationHelper() {}

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T> List<T> requireNonNullList(List<T> list, String message) {
        if (list == null) {
            throw new IllegalArgumentException(message);
        }
        return list;
    }

    public static <T> List<T> requireNonEmptyList(List<T> value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
        return obj;
    }

    public static int requirePositive(int value, String message) {
        if (value <= 0) {
            throw new InvalidTimerConfigurationException(message);
        }
        return value;
    }

    public static int requireNonNegative(int value, String message) {
        if (value < 0) {
            throw new InvalidTimerConfigurationException(message);
        }
        return value;
    }

    public static void validateMuscles(List<String> labels) {
        validateBasicList(labels, "targetMuscleGroups");

        if (labels.isEmpty()) {
            throw new IllegalArgumentException("targetMuscleGroups must contain at least one value");
        }
    }

    public static void validateEquipment(List<String> labels) {
        validateBasicList(labels, "selectedEquipment");
    }

    private static void validateBasicList(List<String> labels, String fieldName) {
        if (labels == null) {
            throw new IllegalArgumentException(fieldName + " list cannot be null");
        }
        for (String label : labels) {
            if (label == null || label.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " entries cannot be blank");
            }
        }
    }
}
