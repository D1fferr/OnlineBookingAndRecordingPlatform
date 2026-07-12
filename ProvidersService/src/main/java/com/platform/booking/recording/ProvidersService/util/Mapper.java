package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class Mapper {

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
}
