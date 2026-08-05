package com.platform.booking.recording.ProvidersService.exceptions;

public class ServiceProviderNotFoundException extends RuntimeException
{
    public ServiceProviderNotFoundException(String message) {
        super(message);
    }
}
