package com.platform.booking.recording.auth_service.KafkaResetPasswordProducerServiceTests;


import com.platform.booking.recording.auth_service.models.ResetPassword;
import com.platform.booking.recording.auth_service.services.KafkaResetPasswordProducerService;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendTest {

    @Mock
    private KafkaTemplate<String, ResetPassword> userResetPasswordKafkaTemplate;

    @InjectMocks
    private KafkaResetPasswordProducerService kafkaResetPasswordProducerService;

    private static final String TRACE_ID_KEY = "traceId";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("send: Successfully sends ResetPassword message to Kafka with traceId header")
    void send_Success() {
        // Arrange
        String traceId = "test-trace-id-123";
        MDC.put(TRACE_ID_KEY, traceId);

        ResetPassword resetPassword = new ResetPassword("user@example.com", "123456", 900L);

        when(userResetPasswordKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaResetPasswordProducerService.send(resetPassword);

        // Assert
        ArgumentCaptor<Message<ResetPassword>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(userResetPasswordKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<ResetPassword> capturedMessage = messageCaptor.getValue();
        assertEquals(resetPassword, capturedMessage.getPayload());
        assertEquals("reset-password-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(traceId, capturedMessage.getHeaders().get(TRACE_ID_KEY));
    }

    @Test
    @DisplayName("send: Throws KafkaException when KafkaTemplate throws an exception")
    void send_KafkaError_ThrowsKafkaException() {
        // Arrange
        ResetPassword resetPassword = new ResetPassword("user@example.com", "123456", 900L);

        CompletableFuture futureWithException = new CompletableFuture<>();
        futureWithException.completeExceptionally(new ExecutionException("Broker unavailable", new RuntimeException()));

        when(userResetPasswordKafkaTemplate.send(any(Message.class))).thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaResetPasswordProducerService.send(resetPassword));

        verify(userResetPasswordKafkaTemplate, times(1)).send(any(Message.class));
    }
}
