package com.platform.booking.recording.email_service.EmailUserDataSenderServiceTests;

import com.platform.booking.recording.email_service.dtos.ProviderCreateDTO;
import com.platform.booking.recording.email_service.services.EmailUserDataSenderService;
import com.platform.booking.recording.email_service.util.MetricsCounter;
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
class RecoverProviderRegistrationTest {

    @Mock
    private MetricsCounter metricsCounter;

    @InjectMocks
    private EmailUserDataSenderService emailUserDataSenderService;

    @Test
    @DisplayName("recover: Increments email failure counter when recovery handles exception for ProviderCreateDTO")
    void recover_ProviderCreateDTO_Success() {
        // Arrange
        UUID providerId = UUID.randomUUID();
        ProviderCreateDTO dto = new ProviderCreateDTO();
        dto.setId(providerId);
        dto.setEmail("failed.provider@example.com");

        Exception causeException = new RuntimeException("SMTP Connection Timeout");

        // Act
        emailUserDataSenderService.recover(causeException, dto);

        // Assert
        verify(metricsCounter, times(1)).incrementEmailCounter("failed_registration_message", "failure");
    }
}
