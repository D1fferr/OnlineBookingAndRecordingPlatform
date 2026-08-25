package com.platform.booking.recording.ProvidersService.KafkaAppointmentProducerServiceTests;

import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentConfirmedForKafkaDTO;
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
class SendToConfirmedKafkaTest {

    private static final String TRACE_ID_KEY = "traceId";

    @Mock
    private KafkaTemplate<String, Object> appointmentCreateKafkaTemplate;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @BeforeEach
    void setUp() {
        MDC.put(TRACE_ID_KEY, "test-trace-id-456");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendToConfirmed: Successfully sends confirmed event to Kafka and updates appointment status")
    void sendToConfirmed_Success() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentConfirmedForKafkaDTO dto = new AppointmentConfirmedForKafkaDTO();
        dto.setSecureToken(secureToken);

        CompletableFuture future = CompletableFuture.completedFuture(null);
        doReturn(future).when(appointmentCreateKafkaTemplate).send(any(Message.class));

        // Act
        kafkaAppointmentProducerService.sendToConfirmed(dto);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Message<AppointmentConfirmedForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(appointmentCreateKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<AppointmentConfirmedForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("appointment-confirmed-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals("test-trace-id-456", capturedMessage.getHeaders().get(TRACE_ID_KEY));

        verify(appointmentService, times(1)).setIsRemindedSentToTrue(secureToken);
    }

    @Test
    @DisplayName("sendToConfirmed: Throws KafkaException when template send fails")
    void sendToConfirmed_KafkaFailure_ThrowsKafkaException() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentConfirmedForKafkaDTO dto = new AppointmentConfirmedForKafkaDTO();
        dto.setSecureToken(secureToken);

        CompletableFuture futureWithException = new CompletableFuture();
        futureWithException.completeExceptionally(new RuntimeException("Kafka topic unavailable"));

        doReturn(futureWithException).when(appointmentCreateKafkaTemplate).send(any(Message.class));

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaAppointmentProducerService.sendToConfirmed(dto));

        verify(appointmentService, never()).setIsRemindedSentToTrue(any());
    }
}