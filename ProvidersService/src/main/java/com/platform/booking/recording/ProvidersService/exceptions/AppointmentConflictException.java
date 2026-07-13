package com.platform.booking.recording.ProvidersService.exceptions;

public class AppointmentConflictException extends RuntimeException {
  public AppointmentConflictException(String message) {
    super(message);
  }
}
