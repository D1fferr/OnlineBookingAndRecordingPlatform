package com.platform.booking.recording.AuthService.UserServiceTests;

import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.services.UserService;
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
class DeleteUserByIdTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("deleteById: Successfully invokes userRepository.deleteById with given UUID")
    void deleteById_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        userService.deleteById(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}
