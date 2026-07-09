package com.patchmaker.coreservice.exception;

public class DuplicateBookmarkException extends RuntimeException {
    public DuplicateBookmarkException(String message) {
        super(message);
    }
}