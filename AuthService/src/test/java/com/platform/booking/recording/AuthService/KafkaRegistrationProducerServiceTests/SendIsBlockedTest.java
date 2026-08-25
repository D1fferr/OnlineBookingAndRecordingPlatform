package com.platform.booking.recording.AuthService.KafkaRegistrationProducerServiceTests;


import com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.AuthService.services.KafkaRegistrationProducerService;
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
class SendIsBlockedTest {

    @Mock
    private KafkaTemplate<String, ProviderIsBlockedDTO> userIsBlockedKafkaTemplate;

    @InjectMocks
    private KafkaRegistrationProducerService kafkaRegistrationProducerService;

    private static final String TRACE_ID_KEY = "traceId";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendIsBlocked: Successfully sends ProviderIsBlockedDTO to user-is-blocked-topic with traceId header")
    void sendIsBlocked_Success() {
        // Arrange
        String traceId = "test-trace-id-blocked-123";
        MDC.put(TRACE_ID_KEY, traceId);

        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO(UUID.randomUUID(), true);

        when(userIsBlockedKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaRegistrationProducerService.sendIsBlocked(dto);

        // Assert
        ArgumentCaptor<Message<ProviderIsBlockedDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(userIsBlockedKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<ProviderIsBlockedDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("user-is-blocked-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(traceId, capturedMessage.getHeaders().get(TRACE_ID_KEY));
    }

    @Test
    @DisplayName("sendIsBlocked: Throws KafkaException when KafkaTemplate fails")
    void sendIsBlocked_KafkaError_ThrowsKafkaException() {
        // Arrange
        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO(UUID.randomUUID(), true);

        CompletableFuture futureWithException = new CompletableFuture<>();
        futureWithException.completeExceptionally(new ExecutionException("Kafka connection error", new RuntimeException()));

        when(userIsBlockedKafkaTemplate.send(any(Message.class))).thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaRegistrationProducerService.sendIsBlocked(dto));

        verify(userIsBlockedKafkaTemplate, times(1)).send(any(Message.class));
    }
}