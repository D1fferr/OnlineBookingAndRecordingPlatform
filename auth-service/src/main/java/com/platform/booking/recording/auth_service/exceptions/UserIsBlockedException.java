package com.platform.booking.recording.auth_service.exceptions;

public class UserIsBlockedException extends RuntimeException {
    public UserIsBlockedException(String message) {
        super("Your account has been blocked. Reason: " + message);
    }
}
