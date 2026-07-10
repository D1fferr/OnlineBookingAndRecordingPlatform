package com.platform.booking.recording.AuthService.exceptions;

public class IncorrectResetCodeException extends RuntimeException {
    public IncorrectResetCodeException(String message) {
        super(message);
    }
}
