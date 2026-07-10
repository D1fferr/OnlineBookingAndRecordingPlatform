package com.platform.booking.recording.AuthService.exceptions;

public class ResetCodeNotFoundException extends RuntimeException {
    public ResetCodeNotFoundException(String message) {
        super(message);
    }
}
