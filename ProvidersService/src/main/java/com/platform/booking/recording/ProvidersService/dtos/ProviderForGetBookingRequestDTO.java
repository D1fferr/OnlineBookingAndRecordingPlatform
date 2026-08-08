package com.platform.booking.recording.ProvidersService.dtos;

import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import com.platform.booking.recording.ProvidersService.models.WorkingHours;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
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
    private List<WorkingHoursGetDTO> workingHours;
    private List<ServiceGetDTO> serviceProviders;
}
