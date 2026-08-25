package com.platform.booking.recording.EmailService.KafkaChangeUserDataConsumerServiceTests;

import com.platform.booking.recording.EmailService.dtos.ProviderCreateDTO;
import com.platform.booking.recording.EmailService.services.EmailUserDataSenderService;
import com.platform.booking.recording.EmailService.services.KafkaChangeUserDataConsumerService;
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
class GetProviderTest {

    @Mock
    private EmailUserDataSenderService emailUserDataSenderService;

    @InjectMocks
    private KafkaChangeUserDataConsumerService kafkaChangeUserDataConsumerService;

    @Test
    @DisplayName("getProvider: Successfully consumes DTO from user-topic and delegates to emailUserDataSenderService")
    void getProvider_Success() {
        // Arrange
        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("provider@example.com");

        // Act
        kafkaChangeUserDataConsumerService.getProvider(dto);

        // Assert
        verify(emailUserDataSenderService, times(1)).sendRegistrationProvider(dto);
    }
}
