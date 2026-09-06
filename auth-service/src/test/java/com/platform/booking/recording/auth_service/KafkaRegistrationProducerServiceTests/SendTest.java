package com.platform.booking.recording.auth_service.KafkaRegistrationProducerServiceTests;

import com.platform.booking.recording.auth_service.dtos.UserForKafkaDTO;
import com.platform.booking.recording.auth_service.services.KafkaRegistrationProducerService;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.AfterEach;
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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendTest {

    @Mock
    private KafkaTemplate<String, UserForKafkaDTO> userRegistrationKafkaTemplate;

    @InjectMocks
    private KafkaRegistrationProducerService kafkaRegistrationProducerService;

    private static final String TRACE_ID_KEY = "traceId";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("send: Successfully sends UserForKafkaDTO to user-topic with traceId header")
    void send_Success() {
        // Arrange
        String traceId = "trace-id-abc-123";
        MDC.put(TRACE_ID_KEY, traceId);

        UserForKafkaDTO dto = new UserForKafkaDTO();
        dto.setId(UUID.randomUUID());
        dto.setEmail("registered.user@example.com");

        when(userRegistrationKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaRegistrationProducerService.send(dto);

        // Assert
        ArgumentCaptor<Message<UserForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(userRegistrationKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<UserForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("user-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(traceId, capturedMessage.getHeaders().get(TRACE_ID_KEY));
    }

    @Test
    @DisplayName("send: Throws KafkaException when sending message fails")
    void send_KafkaError_ThrowsKafkaException() {
        // Arrange
        UserForKafkaDTO dto = new UserForKafkaDTO();
        dto.setId(UUID.randomUUID());

        CompletableFuture futureWithException = new CompletableFuture<>();
        futureWithException.completeExceptionally(new ExecutionException("Kafka connection error", new RuntimeException()));

        when(userRegistrationKafkaTemplate.send(any(Message.class))).thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaRegistrationProducerService.send(dto));

        verify(userRegistrationKafkaTemplate, times(1)).send(any(Message.class));
    }
}