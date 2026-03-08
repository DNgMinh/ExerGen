package com.example.exergen.business.exception;

public class SessionCompletedException extends DomainException {
    public SessionCompletedException() {
        super("No current exercise. The session has already completed.");
    }
}
