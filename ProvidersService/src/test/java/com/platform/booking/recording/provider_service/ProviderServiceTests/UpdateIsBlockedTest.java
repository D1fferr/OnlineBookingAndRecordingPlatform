package com.platform.booking.recording.provider_service.ProviderServiceTests;

import com.platform.booking.recording.provider_service.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.provider_service.exceptions.ProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.services.ProviderService;
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
class UpdateIsBlockedTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("updateIsBlocked: Successfully updates isBlocked status for existing provider")
    void updateIsBlocked_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setIsBlocked(Boolean.FALSE);

        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO();
        dto.setId(providerId);
        dto.setIsBlocked(Boolean.TRUE);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));

        // Act
        providerService.updateIsBlocked(dto);

        // Assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository, times(1)).save(providerCaptor.capture());

        Provider savedProvider = providerCaptor.getValue();
        assertEquals(Boolean.TRUE, savedProvider.getIsBlocked());
    }

    @Test
    @DisplayName("updateIsBlocked: Throws ProviderNotFoundException when provider does not exist")
    void updateIsBlocked_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO();
        dto.setId(providerId);
        dto.setIsBlocked(Boolean.TRUE);

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> providerService.updateIsBlocked(dto));

        verify(providerRepository, never()).save(any());
    }
}
