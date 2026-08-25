package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderChangeDataDTO;
import com.platform.booking.recording.ProvidersService.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.repositories.ProviderRepository;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
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
class UpdateProviderTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("update: Successfully updates non-null fields of the provider")
    void update_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();

        Provider existingProvider = new Provider();
        existingProvider.setId(providerId);
        existingProvider.setName("Old Name");
        existingProvider.setTimezone("UTC");
        existingProvider.setServiceType("Old Service");

        ProviderChangeDataDTO changeDTO = new ProviderChangeDataDTO();
        changeDTO.setName("New Name");
        changeDTO.setTimezone("Europe/Kyiv");
        // serviceType is intentionally left null to test selective update

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(existingProvider));

        // Act
        providerService.update(providerId, changeDTO);

        // Assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository, times(1)).save(providerCaptor.capture());

        Provider updatedProvider = providerCaptor.getValue();
        assertEquals("New Name", updatedProvider.getName());
        assertEquals("Europe/Kyiv", updatedProvider.getTimezone());
        assertEquals("Old Service", updatedProvider.getServiceType()); // Must remain unchanged
    }

    @Test
    @DisplayName("update: Throws ProviderNotFoundException when provider does not exist")
    void update_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        ProviderChangeDataDTO changeDTO = new ProviderChangeDataDTO();

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class,
                () -> providerService.update(providerId, changeDTO));

        verify(providerRepository, never()).save(any());
    }
}
