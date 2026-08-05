package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.ServiceCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.ServiceGetDTO;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceProvider createDTOToEntity(ServiceCreateDTO dto){
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setServiceName(dto.getServiceName());
        serviceProvider.setDuration(dto.getDuration());
        serviceProvider.setPrice(dto.getPrice());
        serviceProvider.setDescription(dto.getDescription());
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
}
