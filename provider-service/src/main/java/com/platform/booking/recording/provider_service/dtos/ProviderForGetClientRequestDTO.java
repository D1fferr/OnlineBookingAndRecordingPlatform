package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ProviderForGetClientRequestDTO {
    private UUID id;
    private String name;
    private String serviceType;
    private String timezone;
    private String avatarURL;
    private List<ServiceProviderForGetClientRequestDTO> serviceProviders;
}
