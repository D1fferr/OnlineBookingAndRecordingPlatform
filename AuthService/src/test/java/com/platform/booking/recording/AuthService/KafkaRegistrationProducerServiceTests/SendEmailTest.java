package com.platform.booking.recording.AuthService.KafkaRegistrationProducerServiceTests;


import com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO;
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
class SendEmailTest {

    @Mock
    private KafkaTemplate<String, ProviderUpdateEmailDTO> userEmailKafkaTemplate;

    @InjectMocks
    private KafkaRegistrationProducerService kafkaRegistrationProducerService;

    private static final String TRACE_ID_KEY = "traceId";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendEmail: Successfully sends ProviderUpdateEmailDTO to user-email-topic with traceId header")
    void sendEmail_Success() {
        // Arrange
        String traceId = "test-trace-id-email-123";
        MDC.put(TRACE_ID_KEY, traceId);

        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO(UUID.randomUUID(), "new.email@example.com");

        when(userEmailKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaRegistrationProducerService.sendEmail(dto);

        // Assert
        ArgumentCaptor<Message<ProviderUpdateEmailDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(userEmailKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<ProviderUpdateEmailDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("user-email-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(traceId, capturedMessage.getHeaders().get(TRACE_ID_KEY));
    }

    @Test
    @DisplayName("sendEmail: Throws KafkaException when KafkaTemplate fails")
    void sendEmail_KafkaError_ThrowsKafkaException() {
        // Arrange
        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO(UUID.randomUUID(), "new.email@example.com");

        CompletableFuture futureWithException = new CompletableFuture<>();
        futureWithException.completeExceptionally(new ExecutionException("Kafka producer failed", new RuntimeException()));

        when(userEmailKafkaTemplate.send(any(Message.class))).thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaRegistrationProducerService.sendEmail(dto));

        verify(userEmailKafkaTemplate, times(1)).send(any(Message.class));
    }
}