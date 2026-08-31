package com.platform.booking.recording.provider_service.ServiceServiceTests;

import com.platform.booking.recording.provider_service.dtos.ServiceCreateDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.ServiceRepository;
import com.platform.booking.recording.provider_service.services.ServiceProviderService;
import com.platform.booking.recording.provider_service.util.ServiceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    @Test
    @DisplayName("save: Successfully sets audit fields and saves service when provider exists")
    void save_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        ServiceCreateDTO createDTO = new ServiceCreateDTO();
        createDTO.setProviderId(providerId);

        Provider provider = new Provider();
        provider.setId(providerId);

        ServiceProvider service = new ServiceProvider();
        service.setId(serviceId);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(serviceMapper.createDTOToEntity(createDTO, provider)).thenReturn(service);

        // Act
        serviceProviderService.save(createDTO);

        // Assert
        ArgumentCaptor<ServiceProvider> serviceCaptor = ArgumentCaptor.forClass(ServiceProvider.class);
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());

        ServiceProvider savedService = serviceCaptor.getValue();
        assertNotNull(savedService.getCreatedAt());
        assertNotNull(savedService.getUpdatedAt());

        verify(providerRepository, times(1)).findById(providerId);
        verify(serviceMapper, times(1)).createDTOToEntity(createDTO, provider);
    }

    @Test
    @DisplayName("save: Throws ProviderNotFoundException when provider does not exist")
    void save_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        ServiceCreateDTO createDTO = new ServiceCreateDTO();
        createDTO.setProviderId(providerId);

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> serviceProviderService.save(createDTO));

        verify(serviceMapper, never()).createDTOToEntity(any(), any());
        verify(serviceRepository, never()).save(any());
    }
}
