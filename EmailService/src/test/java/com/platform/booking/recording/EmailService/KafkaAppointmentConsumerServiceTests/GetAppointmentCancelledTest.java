package com.platform.booking.recording.EmailService.KafkaAppointmentConsumerServiceTests;

import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.services.EmailAppointmentSenderService;
import com.platform.booking.recording.EmailService.services.KafkaAppointmentConsumerService;
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
class GetAppointmentCancelledTest {

    @Mock
    private EmailAppointmentSenderService emailAppointmentSenderService;

    @InjectMocks
    private KafkaAppointmentConsumerService kafkaAppointmentConsumerService;

    @Test
    @DisplayName("getAppointmentCancelled: Successfully consumes DTO from appointment-cancelled-topic and delegates to sendCancelledMessage")
    void getAppointmentCancelled_Success() {
        // Arrange
        AppointmentCancelledDTO dto = new AppointmentCancelledDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail("client@example.com");
        dto.setReason("Client requested cancellation");

        // Act
        kafkaAppointmentConsumerService.getAppointmentCancelled(dto);

        // Assert
        verify(emailAppointmentSenderService, times(1)).sendCancelledMessage(dto);
    }
}
