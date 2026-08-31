package com.platform.booking.recording.provider_service.exceptions;

public class ServiceProviderNotFoundException extends RuntimeException
{
    public ServiceProviderNotFoundException(String message) {
        super(message);
    }
}
