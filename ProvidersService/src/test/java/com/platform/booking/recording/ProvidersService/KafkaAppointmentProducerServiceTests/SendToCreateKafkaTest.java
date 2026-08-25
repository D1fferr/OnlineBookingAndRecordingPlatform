package com.platform.booking.recording.ProvidersService.KafkaAppointmentProducerServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.services.AppointmentService;
import com.platform.booking.recording.ProvidersService.services.KafkaAppointmentProducerService;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendToCreateKafkaTest {

    private static final String TRACE_ID_KEY = "traceId";

    @Mock
    private KafkaTemplate<String, AppointmentCreateForKafkaDTO> appointmentCreateKafkaTemplate;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @BeforeEach
    void setUp() {
        MDC.put(TRACE_ID_KEY, "test-trace-id-123");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendToCreate: Successfully builds message with headers, sends to Kafka, and updates appointment state")
    void sendToCreate_Success() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentCreateForKafkaDTO dto = new AppointmentCreateForKafkaDTO();
        dto.setSecureToken(secureToken);

        when(appointmentCreateKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaAppointmentProducerService.sendToCreate(dto);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Message<AppointmentCreateForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(appointmentCreateKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<AppointmentCreateForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("appointment-create-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals("test-trace-id-123", capturedMessage.getHeaders().get(TRACE_ID_KEY));

        verify(appointmentService, times(1)).setIsRemindedSentToTrue(secureToken);
    }

    @Test
    @DisplayName("sendToCreate: Throws KafkaException when template send fails")
    void sendToCreate_KafkaFailure_ThrowsKafkaException() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentCreateForKafkaDTO dto = new AppointmentCreateForKafkaDTO();
        dto.setSecureToken(secureToken);

        CompletableFuture futureWithException = new CompletableFuture();
        futureWithException.completeExceptionally(new RuntimeException("Kafka cluster unavailable"));

        when(appointmentCreateKafkaTemplate.send(any(Message.class)))
                .thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaAppointmentProducerService.sendToCreate(dto));

        verify(appointmentService, never()).setIsRemindedSentToTrue(any());
    }
}
