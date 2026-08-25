package com.platform.booking.recording.ProvidersService.ServiceServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ServiceUpdateDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ServiceProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import com.platform.booking.recording.ProvidersService.repositories.ServiceRepository;
import com.platform.booking.recording.ProvidersService.services.ServiceProviderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    @Test
    @DisplayName("update: Successfully updates non-null fields and sets updatedAt timestamp")
    void update_Success() {
        // Arrange
        UUID serviceId = UUID.randomUUID();

        ServiceProvider existingService = new ServiceProvider();
        existingService.setId(serviceId);
        existingService.setServiceName("Old Name");
        existingService.setDuration(30);
        existingService.setPrice(100.0);
        existingService.setDescription("Old Description");

        ServiceUpdateDTO updateDTO = new ServiceUpdateDTO();
        updateDTO.setServiceName("New Name");
        updateDTO.setPrice(150.0);
        // duration and description are left null to test partial update

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));

        // Act
        serviceProviderService.update(serviceId, updateDTO);

        // Assert
        ArgumentCaptor<ServiceProvider> serviceCaptor = ArgumentCaptor.forClass(ServiceProvider.class);
        verify(serviceRepository, times(1)).save(serviceCaptor.capture());

        ServiceProvider savedService = serviceCaptor.getValue();
        assertEquals("New Name", savedService.getServiceName());
        assertEquals(150.0, savedService.getPrice());
        assertEquals(30, savedService.getDuration()); // Unchanged
        assertEquals("Old Description", savedService.getDescription()); // Unchanged
        assertNotNull(savedService.getUpdatedAt());
    }

    @Test
    @DisplayName("update: Throws ServiceProviderNotFoundException when service does not exist")
    void update_ServiceNotFound_ThrowsException() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        ServiceUpdateDTO updateDTO = new ServiceUpdateDTO();

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceProviderNotFoundException.class,
                () -> serviceProviderService.update(serviceId, updateDTO));

        verify(serviceRepository, never()).save(any());
    }
}
