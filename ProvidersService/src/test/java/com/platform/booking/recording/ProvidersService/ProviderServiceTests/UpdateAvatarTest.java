package com.platform.booking.recording.ProvidersService.ProviderServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.UserAvatarForKafkaDTO;
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
class UpdateAvatarTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    @DisplayName("updateAvatar: Successfully updates avatar URL for existing provider")
    void updateAvatar_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        String newAvatarUrl = "https://s3.amazonaws.com/bucket/avatar.jpg";

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setAvatarURL("https://s3.amazonaws.com/bucket/old_avatar.jpg");

        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO();
        dto.setId(providerId);
        dto.setAvatarURL(newAvatarUrl);

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));

        // Act
        providerService.updateAvatar(dto);

        // Assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providerRepository, times(1)).save(providerCaptor.capture());

        Provider savedProvider = providerCaptor.getValue();
        assertEquals(newAvatarUrl, savedProvider.getAvatarURL());
    }

    @Test
    @DisplayName("updateAvatar: Throws ProviderNotFoundException when provider does not exist")
    void updateAvatar_ProviderNotFound_ThrowsException() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO();
        dto.setId(providerId);
        dto.setAvatarURL("https://s3.amazonaws.com/bucket/avatar.jpg");

        when(providerRepository.findById(providerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProviderNotFoundException.class, () -> providerService.updateAvatar(dto));

        verify(providerRepository, never()).save(any());
    }
}
