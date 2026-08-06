package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ProviderChangeDataDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderForGetRequestDTO;
import com.platform.booking.recording.ProvidersService.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.ProvidersService.exceptions.FailedSaveImageException;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.util.ProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;
    private final ImageService imageService;

    @Transactional
    public void save(ProviderCreateDTO dto){
        Provider provider = providerMapper.createDTOToProvider(dto);
        provider.setCreatedAt(OffsetDateTime.now());
        providerRepository.save(provider);
        log.atInfo()
                .addKeyValue("providerId", provider.getId())
                .log("The provider was created");
    }
    @Transactional
    public void update(UUID id, ProviderChangeDataDTO dto){
        MDC.put("providerId", id.toString());
        Provider provider = providerRepository.findById(id)
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        if (dto.getName()!=null)
            provider.setName(dto.getName());
        if (dto.getTimezone()!=null)
            provider.setTimezone(dto.getTimezone());
        if (dto.getServiceType()!=null)
            provider.setServiceType(dto.getServiceType());
        providerRepository.save(provider);
        log.atInfo().log("The provider was updated");

    }
    @Transactional
    public void updateEmail(ProviderUpdateEmailDTO dto){
        MDC.put("providerId", dto.getId().toString());
        Provider provider = providerRepository.findById(dto.getId())
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        provider.setEmail(dto.getEmail());
        providerRepository.save(provider);
        log.atInfo().log("The email was updated");
    }
    @Transactional
    public void updateAvatar(UUID id, MultipartFile file){
        MDC.put("providerId", id.toString());
        Provider provider = providerRepository.findById(id)
                .orElseThrow(()->  new ProviderNotFoundException("Provider not found"));
        if (file!=null){
            try {
                String url = imageService.storeImage(file, provider.getId());
                provider.setAvatarURL(url);
            } catch (Exception e) {
                throw new FailedSaveImageException(e.getMessage() + e.getCause());
            }
        }
        providerRepository.save(provider);
        log.atInfo().log("The avatar was updated");

    }

    @Transactional(readOnly = true)
    public ProviderForGetRequestDTO findOneById(UUID id){
        Provider provider = providerRepository.findById(id)
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        return providerMapper.entityToGetRequestDTO(provider);
    }


}
