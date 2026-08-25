package com.platform.booking.recording.AuthService.ResetPasswordServiceTests;


import com.platform.booking.recording.AuthService.dtos.SendCodeDTO;
import com.platform.booking.recording.AuthService.models.ResetPassword;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.redis.ResetPasswordRepository;
import com.platform.booking.recording.AuthService.services.KafkaResetPasswordProducerService;
import com.platform.booking.recording.AuthService.services.ResetPasswordService;
import com.platform.booking.recording.AuthService.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendCodeResetPasswordTest {

    @Mock
    private UserService userService;

    @Mock
    private ResetPasswordRepository resetPasswordRepository;

    @Mock
    private KafkaResetPasswordProducerService kafkaResetPasswordProducerService;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Test
    @DisplayName("sendCode: Successfully generates code, saves to Redis, and sends to Kafka when user exists")
    void sendCode_Success() {
        // Arrange
        String email = "user@example.com";
        SendCodeDTO dto = new SendCodeDTO();
        dto.setEmail(email);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Act
        resetPasswordService.sendCode(dto);

        // Assert
        ArgumentCaptor<ResetPassword> redisCaptor = ArgumentCaptor.forClass(ResetPassword.class);
        verify(resetPasswordRepository, times(1)).save(redisCaptor.capture());

        ResetPassword savedResetPassword = redisCaptor.getValue();
        assertEquals(email, savedResetPassword.getEmail());
        assertNotNull(savedResetPassword.getCode());
        assertEquals(900L, savedResetPassword.getTtlInSeconds()); // 15-min TTL

        verify(kafkaResetPasswordProducerService, times(1)).send(savedResetPassword);
    }

    @Test
    @DisplayName("sendCode: Silently returns and does nothing when user email is not found")
    void sendCode_UserNotFound_DoesNothing() {
        // Arrange
        String email = "unknown@example.com";
        SendCodeDTO dto = new SendCodeDTO();
        dto.setEmail(email);

        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        resetPasswordService.sendCode(dto);

        // Assert
        verify(resetPasswordRepository, never()).save(any());
        verify(kafkaResetPasswordProducerService, never()).send(any());
    }
}