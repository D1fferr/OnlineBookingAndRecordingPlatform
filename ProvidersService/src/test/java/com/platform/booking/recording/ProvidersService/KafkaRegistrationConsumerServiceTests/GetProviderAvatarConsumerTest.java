package com.platform.booking.recording.ProvidersService.KafkaRegistrationConsumerServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.UserAvatarForKafkaDTO;
import com.platform.booking.recording.ProvidersService.services.KafkaRegistrationConsumerService;
import com.platform.booking.recording.ProvidersService.services.ProviderService;
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
class GetProviderAvatarConsumerTest {

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private KafkaRegistrationConsumerService kafkaRegistrationConsumerService;

    @Test
    @DisplayName("getProvider (Avatar): Successfully consumes payload and delegates to ProviderService.updateAvatar")
    void getProvider_AvatarUpdate_Success() {
        // Arrange
        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO();
        dto.setId(UUID.randomUUID());
        dto.setAvatarURL("https://example.com/avatar.jpg");

        // Act
        kafkaRegistrationConsumerService.getProvider(dto);

        // Assert
        verify(providerService, times(1)).updateAvatar(dto);
    }
}
