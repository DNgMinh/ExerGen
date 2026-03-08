package com.example.exergen.business.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BusinessExceptionTest {

    @Test
    public void exerciseNotFoundExceptionContainsExerciseId() {
        ExerciseNotFoundException exception = new ExerciseNotFoundException("ex-404");
        assertEquals("Exercise not found for id: ex-404", exception.getMessage());
    }

    @Test
    public void invalidFilterExceptionUsesProvidedMessage() {
        InvalidFilterException exception = new InvalidFilterException("invalid filter");
        assertEquals("invalid filter", exception.getMessage());
    }

    @Test
    public void invalidTimerConfigurationExceptionUsesProvidedMessage() {
        InvalidTimerConfigurationException exception = new InvalidTimerConfigurationException("invalid timer config");
        assertEquals("invalid timer config", exception.getMessage());
    }
}
