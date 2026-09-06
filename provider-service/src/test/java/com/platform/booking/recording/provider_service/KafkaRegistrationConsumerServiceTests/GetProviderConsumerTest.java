package com.platform.booking.recording.provider_service.KafkaRegistrationConsumerServiceTests;

import com.platform.booking.recording.provider_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.provider_service.services.KafkaRegistrationConsumerService;
import com.platform.booking.recording.provider_service.services.ProviderService;
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
class GetProviderConsumerTest {

    @Mock
    private ProviderService providerService;

    @InjectMocks
    private KafkaRegistrationConsumerService kafkaRegistrationConsumerService;

    @Test
    @DisplayName("getProvider: Successfully consumes payload and delegates to ProviderService.save")
    void getProvider_Success() {
        // Arrange
        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("provider@example.com");

        // Act
        kafkaRegistrationConsumerService.getProvider(dto);

        // Assert
        verify(providerService, times(1)).save(dto);
    }
}
