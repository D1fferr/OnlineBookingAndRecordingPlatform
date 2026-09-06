package com.platform.booking.recording.auth_service.UserServiceTests;

import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.jpa.UserRepository;
import com.platform.booking.recording.auth_service.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaveAfterResetPasswordTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("saveAfterResetPassword: Successfully calls userRepository.save with provided user")
    void saveAfterResetPassword_Success() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPassword("newEncodedPassword");

        // Act
        userService.saveAfterResetPassword(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }
}
