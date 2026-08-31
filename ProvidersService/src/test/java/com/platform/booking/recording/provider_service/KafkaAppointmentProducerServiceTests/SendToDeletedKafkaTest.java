package com.platform.booking.recording.provider_service.KafkaAppointmentProducerServiceTests;

import com.platform.booking.recording.provider_service.dtos.KafkaDTO.AppointmentDeletedForKafkaDTO;
import com.platform.booking.recording.provider_service.services.AppointmentService;
import com.platform.booking.recording.provider_service.services.KafkaAppointmentProducerService;
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
class SendToDeletedKafkaTest {

    private static final String TRACE_ID_KEY = "traceId";

    @Mock
    private KafkaTemplate<String, Object> appointmentCreateKafkaTemplate;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private KafkaAppointmentProducerService kafkaAppointmentProducerService;

    @BeforeEach
    void setUp() {
        MDC.put(TRACE_ID_KEY, "test-trace-id-999");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendToDeleted: Successfully sends deleted event to Kafka")
    void sendToDeleted_Success() {
        // Arrange
        AppointmentDeletedForKafkaDTO dto = new AppointmentDeletedForKafkaDTO();
        dto.setSecureToken(UUID.randomUUID());

        CompletableFuture future = CompletableFuture.completedFuture(null);
        doReturn(future).when(appointmentCreateKafkaTemplate).send(any(Message.class));

        // Act
        kafkaAppointmentProducerService.sendToDeleted(dto);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Message<AppointmentDeletedForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(appointmentCreateKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<AppointmentDeletedForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("appointment-deleted-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals("test-trace-id-999", capturedMessage.getHeaders().get(TRACE_ID_KEY));

        verifyNoInteractions(appointmentService);
    }

    @Test
    @DisplayName("sendToDeleted: Throws KafkaException when template send fails")
    void sendToDeleted_KafkaFailure_ThrowsKafkaException() {
        // Arrange
        AppointmentDeletedForKafkaDTO dto = new AppointmentDeletedForKafkaDTO();

        CompletableFuture futureWithException = new CompletableFuture();
        futureWithException.completeExceptionally(new RuntimeException("Kafka broker failure"));

        doReturn(futureWithException).when(appointmentCreateKafkaTemplate).send(any(Message.class));

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaAppointmentProducerService.sendToDeleted(dto));
        verifyNoInteractions(appointmentService);
    }
}
