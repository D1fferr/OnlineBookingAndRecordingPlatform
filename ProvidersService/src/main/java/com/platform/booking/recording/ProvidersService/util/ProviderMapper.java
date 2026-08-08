package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetBookingRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetClientRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetRequestDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import com.platform.booking.recording.ProvidersService.models.WorkingHours;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProviderMapper {

    private final ServiceMapper serviceMapper;
    private final WorkingHoursMapper workingHoursMapper;
    public Provider createDTOToProvider(ProviderCreateDTO dto){
        var provider = new Provider();
        provider.setId(dto.getId());
        provider.setName(dto.getName());
        provider.setEmail(dto.getEmail());
        provider.setServiceType(dto.getServiceType());
        provider.setTimezone(dto.getTimezone());
        provider.setAvatarURL(dto.getAvatarURL());
        return provider;
    }
    public ProviderForGetRequestDTO entityToGetRequestDTO(Provider provider){
        var dto = new ProviderForGetRequestDTO();
        dto.setName(provider.getName());
        dto.setTimezone(provider.getTimezone());
        dto.setServiceType(provider.getServiceType());
        return dto;
    }
    public ProviderForGetClientRequestDTO entityToGetForClientRequestDTO(Provider provider){
        var dto = new ProviderForGetClientRequestDTO();
        dto.setName(provider.getName());
        dto.setTimezone(provider.getTimezone());
        dto.setId(provider.getId());
        dto.setServiceType(provider.getServiceType());
        dto.setAvatarURL(provider.getAvatarURL());
        dto.setServiceProviders(provider.getServiceProviders()
                .stream()
                .map(serviceMapper::entityToGetClientRequest)
                .toList());
        return dto;
    }
    public ProviderForGetBookingRequestDTO entityToGetBookingRequestDTO(Provider provider){
        var dto = new ProviderForGetBookingRequestDTO();
        dto.setName(provider.getName());
        dto.setTimezone(provider.getTimezone());
        dto.setId(provider.getId());
        dto.setServiceType(provider.getServiceType());
        dto.setAvatarURL(provider.getAvatarURL());
        dto.setServiceProviders(provider.getServiceProviders()
                .stream()
                .map(serviceMapper::entityToGetDTO)
                .toList());
        dto.setWorkingHours(provider.getWorkingHours()
                .stream()
                .map(workingHoursMapper::entityToListGetDTO)
                .toList());
        return dto;

    }


}
