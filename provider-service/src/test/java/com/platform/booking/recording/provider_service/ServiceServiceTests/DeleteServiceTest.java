package com.platform.booking.recording.provider_service.ServiceServiceTests;

import com.platform.booking.recording.provider_service.repositories.ServiceRepository;
import com.platform.booking.recording.provider_service.services.ServiceProviderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceProviderService serviceProviderService;

    @Test
    @DisplayName("delete: Successfully calls repository deleteById with provided UUID")
    void delete_Success() {
        // Arrange
        UUID serviceId = UUID.randomUUID();

        // Act
        serviceProviderService.delete(serviceId);

        // Assert
        verify(serviceRepository, times(1)).deleteById(serviceId);
    }
}
