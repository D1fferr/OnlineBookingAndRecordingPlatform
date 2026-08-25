package com.platform.booking.recording.AuthService.KafkaRegistrationProducerServiceTests;


import com.platform.booking.recording.AuthService.dtos.UserAvatarForKafkaDTO;
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
class SendAvatarTest {

    @Mock
    private KafkaTemplate<String, UserAvatarForKafkaDTO> userAvatarKafkaTemplate;

    @InjectMocks
    private KafkaRegistrationProducerService kafkaRegistrationProducerService;

    private static final String TRACE_ID_KEY = "traceId";

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("sendAvatar: Successfully sends UserAvatarForKafkaDTO to user-avatar-topic with traceId header")
    void sendAvatar_Success() {
        // Arrange
        String traceId = "test-trace-id-avatar-123";
        MDC.put(TRACE_ID_KEY, traceId);

        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO(UUID.randomUUID(), "/api/images/avatar.png");

        when(userAvatarKafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        kafkaRegistrationProducerService.sendAvatar(dto);

        // Assert
        ArgumentCaptor<Message<UserAvatarForKafkaDTO>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(userAvatarKafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<UserAvatarForKafkaDTO> capturedMessage = messageCaptor.getValue();
        assertEquals(dto, capturedMessage.getPayload());
        assertEquals("user-avatar-topic", capturedMessage.getHeaders().get(KafkaHeaders.TOPIC));
        assertEquals(traceId, capturedMessage.getHeaders().get(TRACE_ID_KEY));
    }

    @Test
    @DisplayName("sendAvatar: Throws KafkaException when KafkaTemplate fails")
    void sendAvatar_KafkaError_ThrowsKafkaException() {
        // Arrange
        UserAvatarForKafkaDTO dto = new UserAvatarForKafkaDTO(UUID.randomUUID(), "/api/images/avatar.png");

        CompletableFuture futureWithException = new CompletableFuture<>();
        futureWithException.completeExceptionally(new ExecutionException("Kafka producer failed", new RuntimeException()));

        when(userAvatarKafkaTemplate.send(any(Message.class))).thenReturn(futureWithException);

        // Act & Assert
        assertThrows(KafkaException.class, () -> kafkaRegistrationProducerService.sendAvatar(dto));

        verify(userAvatarKafkaTemplate, times(1)).send(any(Message.class));
    }
}