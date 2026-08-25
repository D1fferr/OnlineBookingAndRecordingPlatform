package com.platform.booking.recording.ProvidersService.KafkaAppointmentProducerServiceTests;


import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.AppointmentCancelledForKafkaDTO;
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
class SendToCancelledKafkaTest {

    private static final String TRACE_ID_KEY = "traceId";

    @Mock
    private KafkaTemplate<String, Object> appointmentCancelledKafkaTemplate;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @BeforeEach
    void setUp() {
        MDC.put(TRACE_ID_KEY, "test-trace-id-789");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendToCancelled: Successfully sends cancelled event to Kafka and updates appointment status")
    void sendToCancelled_Success() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentCancelledForKafkaDTO dto = new AppointmentCancelledForKafkaDTO();
        dto.setSecureToken(secureToken);

        CompletableFuture future = CompletableFuture.completedFuture(null);
        doReturn(future).when(appointmentCancelledKafkaTemplate).send(any(Message.class));

        // Act
        kafkaAppointmentProducerService.sendToCancelled(dto);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Message<AppointmentCancelledForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(appointmentCancelledKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<AppointmentCancelledForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("appointment-cancelled-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals("test-trace-id-789", capturedMessage.getHeaders().get(TRACE_ID_KEY));

        verify(appointmentService, times(1)).setIsRemindedSentToTrue(secureToken);
    }

    @Test
    @DisplayName("sendToCancelled: Throws KafkaException when template send fails")
    void sendToCancelled_KafkaFailure_ThrowsKafkaException() {
        // Arrange
        UUID secureToken = UUID.randomUUID();
        AppointmentCancelledForKafkaDTO dto = new AppointmentCancelledForKafkaDTO();
        dto.setSecureToken(secureToken);

        CompletableFuture futureWithException = new CompletableFuture();
        futureWithException.completeExceptionally(new RuntimeException("Kafka connection timeout"));

        doReturn(futureWithException).when(appointmentCancelledKafkaTemplate).send(any(Message.class));

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaAppointmentProducerService.sendToCancelled(dto));

        verify(appointmentService, never()).setIsRemindedSentToTrue(any());
    }
}