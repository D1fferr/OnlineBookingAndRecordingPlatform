package com.platform.booking.recording.auth_service.exceptions;

public class ResetCodeNotFoundException extends RuntimeException {
    public ResetCodeNotFoundException(String message) {
        super(message);
    }
}
