package com.example.exergen.model;

import java.util.List;

final class ModelValidation {
    private ModelValidation() {
    }

    static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    static <T> List<T> requireNonEmptyList(List<T> value, String message) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
