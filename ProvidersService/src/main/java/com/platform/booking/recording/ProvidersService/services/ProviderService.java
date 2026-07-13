package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.util.ProviderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;


    @Transactional
    public void save(ProviderCreateDTO dto){
        Provider provider = providerMapper.createDTOToProvider(dto);
        provider.setCreatedAt(OffsetDateTime.now());
        providerRepository.save(provider);
    }
    @Transactional(readOnly = true)
    public Optional<Provider> findById(UUID id){
        return providerRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public String findEmailById(UUID id){
        Provider provider = providerRepository.findById(id)
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        return provider.getEmail();
    }


}
