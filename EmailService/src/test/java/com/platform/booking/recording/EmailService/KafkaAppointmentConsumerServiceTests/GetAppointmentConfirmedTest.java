package com.platform.booking.recording.EmailService.KafkaAppointmentConsumerServiceTests;

import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
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
class GetAppointmentConfirmedTest {

    @Mock
    private EmailAppointmentSenderService emailAppointmentSenderService;

    @InjectMocks
    private KafkaAppointmentConsumerService kafkaAppointmentConsumerService;

    @Test
    @DisplayName("getAppointmentConfirmed: Successfully consumes DTO from appointment-confirmed-topic and delegates to sendConfirmedMessage")
    void getAppointmentConfirmed_Success() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail("client@example.com");

        // Act
        kafkaAppointmentConsumerService.getAppointmentConfirmed(dto);

        // Assert
        verify(emailAppointmentSenderService, times(1)).sendConfirmedMessage(dto);
    }
}
