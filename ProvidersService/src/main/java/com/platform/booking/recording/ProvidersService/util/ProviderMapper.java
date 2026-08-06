package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetRequestDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import org.springframework.stereotype.Component;

@Component
public class ProviderMapper {

    public Provider createDTOToProvider(ProviderCreateDTO dto){
        Provider provider = new Provider();
        provider.setId(dto.getId());
        provider.setName(dto.getName());
        provider.setEmail(dto.getEmail());
        provider.setServiceType(dto.getServiceType());
        provider.setTimezone(dto.getTimezone());
        provider.setAvatarURL(dto.getAvatarURL());
        return provider;
    }
    public ProviderForGetRequestDTO entityToGetRequestDTO(Provider provider){
        ProviderForGetRequestDTO dto = new ProviderForGetRequestDTO();
        dto.setName(provider.getName());
        dto.setTimezone(provider.getTimezone());
        dto.setServiceType(provider.getServiceType());
        return dto;
    }

}
