package com.platform.booking.recording.EmailService.EmailAppointmentSenderServiceTests;

import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.EmailService.services.EmailAppointmentSenderService;
import com.platform.booking.recording.EmailService.util.MetricsCounter;
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
class RecoverAppointmentCreateTest {

    @Mock
    private MetricsCounter metricsCounter;

    @InjectMocks
    private EmailAppointmentSenderService emailAppointmentSenderService;

    @Test
    @DisplayName("recover: Increments failed_appointment counter when handling exception for AppointmentCreateDTO")
    void recover_AppointmentCreateDTO_Success() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setProviderEmail("provider@example.com");
        dto.setClientName("John Doe");

        Exception causeException = new RuntimeException("SMTP connection failed after retries");

        // Act
        emailAppointmentSenderService.recover(causeException, dto);

        // Assert
        verify(metricsCounter, times(1)).incrementEmailCounter("failed_appointment", "failure");
    }
}