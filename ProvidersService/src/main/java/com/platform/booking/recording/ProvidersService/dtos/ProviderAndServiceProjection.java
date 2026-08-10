package com.platform.booking.recording.ProvidersService.dtos;

import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ProviderAndServiceProjection {
    private final Provider provider;
    private final ServiceProvider service;
}
