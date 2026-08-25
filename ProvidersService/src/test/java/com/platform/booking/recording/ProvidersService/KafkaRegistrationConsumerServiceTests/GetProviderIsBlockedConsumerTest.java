package com.platform.booking.recording.ProvidersService.KafkaRegistrationConsumerServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.ProviderIsBlockedDTO;
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
class GetProviderIsBlockedConsumerTest {

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private KafkaRegistrationConsumerService kafkaRegistrationConsumerService;

    @Test
    @DisplayName("getProvider (IsBlocked): Successfully consumes payload and delegates to ProviderService.updateIsBlocked")
    void getProvider_IsBlockedUpdate_Success() {
        // Arrange
        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO();
        dto.setId(UUID.randomUUID());
        dto.setIsBlocked(Boolean.TRUE);

        // Act
        kafkaRegistrationConsumerService.getProvider(dto);

        // Assert
        verify(providerService, times(1)).updateIsBlocked(dto);
    }
}
