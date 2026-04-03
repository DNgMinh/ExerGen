package com.example.exergen.business.service;

import com.example.exergen.business.exception.StatisticsValidationException;
import org.junit.Test;

public class StatisticsValidationTest {

    @Test
    public void testRequireNonNegative_ValidValue_Success() {
        StatisticsValidation.requireNonNegative(0, "message");
        StatisticsValidation.requireNonNegative(10, "message");
    }

    @Test(expected = StatisticsValidationException.class)
    public void testRequireNonNegative_NegativeValue_ThrowsException() {
        StatisticsValidation.requireNonNegative(-1, "message");
    }

    @Test
    public void testRequirePositive_ValidValue_Success() {
        StatisticsValidation.requirePositive(1L, "message");
    }

    @Test(expected = StatisticsValidationException.class)
    public void testRequirePositive_ZeroValue_ThrowsException() {
        StatisticsValidation.requirePositive(0L, "message");
    }

    @Test(expected = StatisticsValidationException.class)
    public void testRequirePositive_NegativeValue_ThrowsException() {
        StatisticsValidation.requirePositive(-1L, "message");
    }

    @Test
    public void testRequireNonNull_ValidValue_Success() {
        StatisticsValidation.requireNonNull(new Object(), "message");
    }

    @Test(expected = StatisticsValidationException.class)
    public void testRequireNonNull_NullValue_ThrowsException() {
        StatisticsValidation.requireNonNull(null, "message");
    }
}
