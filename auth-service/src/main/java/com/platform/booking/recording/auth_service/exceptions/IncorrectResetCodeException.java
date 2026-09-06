package com.platform.booking.recording.auth_service.exceptions;

public class IncorrectResetCodeException extends RuntimeException {
    public IncorrectResetCodeException(String message) {
        super(message);
    }
}
