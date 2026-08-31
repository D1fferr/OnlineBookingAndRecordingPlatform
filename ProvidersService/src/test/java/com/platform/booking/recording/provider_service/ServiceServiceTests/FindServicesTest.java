package com.platform.booking.recording.provider_service.ServiceServiceTests;

import com.platform.booking.recording.provider_service.dtos.ServiceGetDTO;
import com.platform.booking.recording.provider_service.dtos.ServicePageDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.ServiceRepository;
import com.platform.booking.recording.provider_service.services.ServiceProviderService;
import com.platform.booking.recording.provider_service.util.ServiceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindServicesTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    @Test
    @DisplayName("findServices: Successfully returns mapped page of services for provider")
    void findServices_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(UUID.randomUUID());

        ServiceGetDTO getDTO = new ServiceGetDTO();

        Page<ServiceProvider> servicePage = new PageImpl<>(List.of(serviceProvider), pageable, 1);

        when(providerRepository.existsById(providerId)).thenReturn(true);
        when(serviceRepository.findAllByProvider_Id(eq(providerId), eq(pageable))).thenReturn(servicePage);
        when(serviceMapper.entityToGetDTO(serviceProvider)).thenReturn(getDTO);

        // Act
        ServicePageDTO result = serviceProviderService.findServices(providerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(providerId, result.getProviderId());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getDtos().size());
        assertEquals(getDTO, result.getDtos().get(0));

        verify(providerRepository, times(1)).existsById(providerId);
        verify(serviceRepository, times(1)).findAllByProvider_Id(providerId, pageable);
        verify(serviceMapper, times(1)).entityToGetDTO(serviceProvider);
    }

    @Test
    @DisplayName("findServices: Throws ProviderNotFoundException when provider does not exist")
    void findServices_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(providerRepository.existsById(providerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ProviderNotFoundException.class,
                () -> serviceProviderService.findServices(providerId, pageable));

        verify(serviceRepository, never()).findAllByProvider_Id(any(), any());
        verify(serviceMapper, never()).entityToGetDTO(any());
    }
}