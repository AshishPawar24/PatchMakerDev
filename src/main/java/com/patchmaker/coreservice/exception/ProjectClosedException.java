package com.patchmaker.coreservice.exception;

public class ProjectClosedException extends RuntimeException {
    public ProjectClosedException(String message) {
        super(message);
    }
}