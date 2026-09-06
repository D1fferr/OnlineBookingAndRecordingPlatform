package com.platform.booking.recording.provider_service.util;

import com.platform.booking.recording.provider_service.dtos.ServiceCreateDTO;
import com.platform.booking.recording.provider_service.dtos.ServiceGetDTO;
import com.platform.booking.recording.provider_service.dtos.ServiceProviderForGetClientRequestDTO;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceProvider createDTOToEntity(ServiceCreateDTO dto, Provider provider){
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceName(dto.getServiceName());
        serviceProvider.setDuration(dto.getDuration());
        serviceProvider.setPrice(dto.getPrice());
        serviceProvider.setDescription(dto.getDescription());
        serviceProvider.setProvider(provider);
        return serviceProvider;
    }
    public ServiceGetDTO entityToGetDTO(ServiceProvider serviceProvider){
        ServiceGetDTO dto = new ServiceGetDTO();
        dto.setId(serviceProvider.getId());
        dto.setServiceName(serviceProvider.getServiceName());
        dto.setDuration(serviceProvider.getDuration());
        dto.setPrice(serviceProvider.getPrice());
        dto.setDescription(serviceProvider.getDescription());
        return dto;
    }
    public ServiceProviderForGetClientRequestDTO entityToGetClientRequest(ServiceProvider serviceProvider){
        var dto = new ServiceProviderForGetClientRequestDTO();
        dto.setServiceName(serviceProvider.getServiceName());
        dto.setDuration(serviceProvider.getDuration());
        dto.setPrice(serviceProvider.getPrice());
        return dto;
    }
}
