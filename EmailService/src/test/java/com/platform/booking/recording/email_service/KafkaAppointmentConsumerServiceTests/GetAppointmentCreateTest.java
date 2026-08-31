package com.platform.booking.recording.email_service.KafkaAppointmentConsumerServiceTests;

import com.platform.booking.recording.email_service.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.email_service.services.EmailAppointmentSenderService;
import com.platform.booking.recording.email_service.services.KafkaAppointmentConsumerService;
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
class GetAppointmentCreateTest {

    @Mock
    private EmailAppointmentSenderService emailAppointmentSenderService;

    @InjectMocks
    private KafkaAppointmentConsumerService kafkaAppointmentConsumerService;

    @Test
    @DisplayName("getAppointmentCreate: Successfully delegates appointment creation DTO to client and provider email senders")
    void getAppointmentCreate_Success() {
        // Arrange
        AppointmentCreateDTO dto = new AppointmentCreateDTO();
        dto.setSecureToken(UUID.randomUUID());
        dto.setClientEmail("client@example.com");
        dto.setProviderEmail("provider@example.com");

        // Act
        kafkaAppointmentConsumerService.getAppointmentCreate(dto);

        // Assert
        verify(emailAppointmentSenderService, times(1)).sendCreateMessageToClient(dto);
        verify(emailAppointmentSenderService, times(1)).sendCreateMessageToProvider(dto);
    }
}
