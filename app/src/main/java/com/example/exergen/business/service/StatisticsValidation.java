package com.example.exergen.business.service;

import com.example.exergen.business.exception.StatisticsValidationException;

public final class StatisticsValidation {
    private StatisticsValidation() {
    }

    public static void requireNonNegative(int value, String message) {
        if (value < 0) {
            throw new StatisticsValidationException(message);
        }
    }

    public static void requirePositive(long value, String message) {
        if (value <= 0L) {
            throw new StatisticsValidationException(message);
        }
    }

    public static void requireNonNull(Object value, String message) {
        if (value == null) {
            throw new StatisticsValidationException(message);
        }
    }
}
