package com.platform.booking.recording.AuthService.exceptions;

public class ValidationUserException extends RuntimeException {
    public ValidationUserException(String message) {
        super(message);
    }
}
