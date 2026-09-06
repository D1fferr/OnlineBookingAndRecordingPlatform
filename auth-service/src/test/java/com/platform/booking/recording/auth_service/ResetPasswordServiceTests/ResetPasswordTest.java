package com.platform.booking.recording.auth_service.ResetPasswordServiceTests;

import com.platform.booking.recording.auth_service.dtos.ResetPasswordDTO;
import com.platform.booking.recording.auth_service.exceptions.IncorrectResetCodeException;
import com.platform.booking.recording.auth_service.models.ResetPassword;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.redis.ResetPasswordRepository;
import com.platform.booking.recording.auth_service.services.ResetPasswordService;
import com.platform.booking.recording.auth_service.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ResetPasswordRepository resetPasswordRepository;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Test
    @DisplayName("resetPassword: Successfully resets password when user exists and code matches Redis record")
    void resetPassword_Success() {
        // Arrange
        String email = "user@example.com";
        String code = "123456";
        String rawPassword = "newSecretPassword123";
        String encodedPassword = "encodedSecretPassword123";

        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);
        dto.setCode(code);
        dto.setNewPassword(rawPassword);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        ResetPassword redisRecord = new ResetPassword(email, code, 900L);

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
        // validateCode під капотом шукає запис у Redis за email чи кодом
        when(resetPasswordRepository.findById(email)).thenReturn(Optional.of(redisRecord));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // Act
        resetPasswordService.resetPassword(dto);

        // Assert
        assertEquals(encodedPassword, user.getPassword());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userService, times(1)).saveAfterResetPassword(user);
    }

    @Test
    @DisplayName("resetPassword: Throws IncorrectResetCodeException when code in Redis does not match or is absent")
    void resetPassword_InvalidCode_ThrowsIncorrectResetCodeException() {
        // Arrange
        String email = "user@example.com";
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);
        dto.setCode("999999");
        dto.setNewPassword("newPassword123");

        User user = new User();
        user.setEmail(email);

        when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
        // Повертаємо порожній Optional або запис з іншим кодом
        when(resetPasswordRepository.findById(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IncorrectResetCodeException.class, () -> resetPasswordService.resetPassword(dto));

        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).saveAfterResetPassword(any());
    }

    @Test
    @DisplayName("resetPassword: Silently returns when user is not found by email")
    void resetPassword_UserNotFound_DoesNothing() {
        // Arrange
        String email = "unknown@example.com";
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);

        when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

        // Act
        resetPasswordService.resetPassword(dto);

        // Assert
        verify(resetPasswordRepository, never()).findById(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userService, never()).saveAfterResetPassword(any());
    }
}