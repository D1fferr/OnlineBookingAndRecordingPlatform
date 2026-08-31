package com.platform.booking.recording.provider_service.dtos;

import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ProviderAndServiceProjection {
    private final Provider provider;
    private final ServiceProvider service;
}
