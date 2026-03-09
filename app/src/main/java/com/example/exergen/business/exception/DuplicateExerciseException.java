package com.example.exergen.business.exception;

public class DuplicateExerciseException extends DomainException {
    public DuplicateExerciseException(String exerciseId) {
        super("Exercise with id already exists: " + exerciseId);
    }
}
