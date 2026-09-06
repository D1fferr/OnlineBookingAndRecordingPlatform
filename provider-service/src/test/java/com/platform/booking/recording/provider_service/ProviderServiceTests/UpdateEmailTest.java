package com.platform.booking.recording.provider_service.ProviderServiceTests;

import com.platform.booking.recording.provider_service.dtos.ProviderUpdateEmailDTO;
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
class UpdateEmailTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("updateEmail: Successfully updates email address for existing provider")
    void updateEmail_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        String newEmail = "new.email@example.com";

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setEmail("old.email@example.com");

        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO();
        dto.setId(providerId);
        dto.setEmail(newEmail);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));

        // Act
        providerService.updateEmail(dto);

        // Assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository, times(1)).save(providerCaptor.capture());

        Provider savedProvider = providerCaptor.getValue();
        assertEquals(newEmail, savedProvider.getEmail());
    }

    @Test
    @DisplayName("updateEmail: Throws ProviderNotFoundException when provider does not exist")
    void updateEmail_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO();
        dto.setId(providerId);
        dto.setEmail("new.email@example.com");

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> providerService.updateEmail(dto));

        verify(providerRepository, never()).save(any());
    }
}