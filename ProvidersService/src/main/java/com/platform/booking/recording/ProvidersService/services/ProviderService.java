package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.*;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.util.ProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        provider.setIsBlocked(Boolean.FALSE);
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
    public void updateIsBlocked(ProviderIsBlockedDTO dto){
        MDC.put("providerId", dto.getId().toString());
        Provider provider = providerRepository.findById(dto.getId())
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        provider.setIsBlocked(dto.getIsBlocked());
        providerRepository.save(provider);
        log.atInfo().log("The isBlocked field was updated");
    }

    @Transactional
    public void updateAvatar(UserAvatarForKafkaDTO dto){
        MDC.put("providerId", dto.getId().toString());
        Provider provider = providerRepository.findById(dto.getId())
                .orElseThrow(()->  new ProviderNotFoundException("Provider not found"));
        provider.setAvatarURL(dto.getAvatarURL());
        providerRepository.save(provider);
        log.atInfo().log("The avatar was updated");
    }

    @Transactional(readOnly = true)
    public ProviderForGetRequestDTO findOneById(UUID id){
        MDC.put("providerId", id.toString());
        Provider provider = providerRepository.findById(id)
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        return providerMapper.entityToGetRequestDTO(provider);
    }

    @Transactional(readOnly = true)
    public ProviderPageForGetClientRequestDTO findProvidersForClient(String search, String category, Pageable pageable){
        String searchPattern;
        if (search==null){
            searchPattern = null;
        }else {
            searchPattern = "%" + search.replaceAll("\\s+", "%") + "%";
        }
        var dto = new ProviderPageForGetClientRequestDTO();
        Page<UUID> idPage = providerRepository.findProviderIds(searchPattern, category, pageable);
        if (idPage.isEmpty()) {
            dto.setDtos(List.of());
            dto.setTotalElements(0L);
            dto.setTotalPages(0);
            return dto;
        }
        List<Provider> providers = providerRepository.findAllByIdsIn(idPage.getContent());
        Map<UUID, Provider> providerMap = providers.stream()
                .collect(Collectors.toMap(Provider::getId, p -> p));
        List<ProviderForGetClientRequestDTO> sortedProviders = idPage.getContent().stream()
                .map(providerMap::get)
                .filter(Objects::nonNull)
                .map(providerMapper::entityToGetForClientRequestDTO)
                .toList();
        dto.setDtos(sortedProviders);
        dto.setTotalPages(idPage.getTotalPages());
        dto.setTotalElements(idPage.getTotalElements());
        return dto;
    }
    @Transactional(readOnly = true)
    public ProviderListServiceTypeDTO finAllCategories(){
        return new ProviderListServiceTypeDTO(providerRepository.findAllUniqueServiceTypes());
    }
    @Transactional(readOnly = true)
    public ProviderForGetBookingRequestDTO findOneByIdForBooking(UUID id){
        MDC.put("providerId", id.toString());
        Provider provider = providerRepository.findByIdAndIsBlocked(id, Boolean.FALSE)
                .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
        return providerMapper.entityToGetBookingRequestDTO(provider);
    }

}
