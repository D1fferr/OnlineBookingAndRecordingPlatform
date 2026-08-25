package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.ServiceCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ServicePageDTO;
import com.platform.booking.recording.ProvidersService.dtos.ServiceUpdateDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.exceptions.ServiceProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.repositories.ServiceRepository;
import com.platform.booking.recording.ProvidersService.util.ServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceProviderService {
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final ProviderRepository providerRepository;

    @Transactional
    public void save(ServiceCreateDTO dto){
        MDC.put("providerId", dto.getProviderId().toString());
        Provider provider  = providerRepository.findById(dto.getProviderId())
                    .orElseThrow(()->new ProviderNotFoundException("Provider not found"));
            ServiceProvider service = serviceMapper.createDTOToEntity(dto, provider);
            service.setCreatedAt(OffsetDateTime.now());
            service.setUpdatedAt(OffsetDateTime.now());
            serviceRepository.save(service);
            MDC.put("serviceId", service.getId().toString());
            log.atInfo().log("Service created");
    }
    @Transactional
    public void update(UUID id, ServiceUpdateDTO dto){
        MDC.put("serviceId", id.toString());
        ServiceProvider serviceProvider = serviceRepository.findById(id)
                .orElseThrow(()-> new ServiceProviderNotFoundException("Service bon found"));
        if (dto.getServiceName()!=null)
            serviceProvider.setServiceName(dto.getServiceName());
        if (dto.getDuration()!=null)
            serviceProvider.setDuration(dto.getDuration());
        if (dto.getPrice()!=null)
            serviceProvider.setPrice(dto.getPrice());
        if (dto.getDescription()!=null)
           serviceProvider.setDescription(dto.getDescription());
        serviceProvider.setUpdatedAt(OffsetDateTime.now());
        serviceRepository.save(serviceProvider);
        log.atInfo().log("Service updated");
    }
    @Transactional(readOnly = true)
    public ServicePageDTO findServices(UUID id, Pageable pageable){
        MDC.put("providerId", id.toString());
        if (!providerRepository.existsById(id))
            throw new ProviderNotFoundException("Provider not found");
        ServicePageDTO servicePageDTO = new ServicePageDTO();
        Page<ServiceProvider> serviceProviderPage = serviceRepository.findAllByProvider_Id(id, pageable);
        servicePageDTO.setDtos(serviceProviderPage
                .getContent()
                .stream()
                .map(serviceMapper::entityToGetDTO)
                .toList());
        servicePageDTO.setTotalPages(serviceProviderPage.getTotalPages());
        servicePageDTO.setTotalElements(serviceProviderPage.getTotalElements());
        servicePageDTO.setProviderId(id);
        return servicePageDTO;
    }
    @Transactional
    public void delete(UUID id){
        serviceRepository.deleteById(id);
    }


}
