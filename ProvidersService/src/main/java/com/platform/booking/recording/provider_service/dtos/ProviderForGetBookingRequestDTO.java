package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ProviderForGetBookingRequestDTO {
    private UUID id;
    private String name;
    private String serviceType;
    private String timezone;
    private String avatarURL;
    private Set<WorkingHoursGetDTO> workingHours;
    private Set<ServiceGetDTO> serviceProviders;
}
