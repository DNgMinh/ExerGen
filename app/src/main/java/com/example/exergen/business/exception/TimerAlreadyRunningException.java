package com.example.exergen.business.exception;

public class TimerAlreadyRunningException extends DomainException {
    public TimerAlreadyRunningException() {
        super("Timer is already running.");
    }
}
