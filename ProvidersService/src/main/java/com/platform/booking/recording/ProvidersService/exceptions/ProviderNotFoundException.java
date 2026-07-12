package com.platform.booking.recording.ProvidersService.exceptions;

public class ProviderNotFoundException extends RuntimeException {
  public ProviderNotFoundException(String message) {
    super(message);
  }
}
