package com.example.exergen.business.exception;

public class ExerciseNotFoundException extends DomainException {
    public ExerciseNotFoundException(String exerciseId) {
        super("Exercise not found for id: " + exerciseId);
    }
}
