package com.patchmaker.coreservice.exception;

public record ErrorResponse(boolean success, String message) {
}