package com.platform.booking.recording.AuthService.exceptions;

public class UserIsBlockedException extends RuntimeException {
    public UserIsBlockedException(String message) {
        super("Your account has been blocked. Reason: " + message);
    }
}
