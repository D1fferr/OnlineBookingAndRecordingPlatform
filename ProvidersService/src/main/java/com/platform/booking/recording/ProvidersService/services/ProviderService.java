package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final Mapper mapper;


    @Transactional
    public void save(ProviderCreateDTO dto){
        Provider provider = mapper.createDTOToProvider(dto);
        provider.setCreatedAt(OffsetDateTime.now());
        providerRepository.save(provider);
    }


}
