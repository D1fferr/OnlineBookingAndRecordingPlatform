package com.platform.booking.recording.auth_service.UserServiceTests;

import com.platform.booking.recording.auth_service.exceptions.UserNotFoundException;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.jpa.UserRepository;
import com.platform.booking.recording.auth_service.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserByIdTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("findUserById: Successfully returns user when exists")
    void findUserById_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User actualUser = userService.findUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
        assertEquals(userId, actualUser.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("findUserById: Throws UserNotFoundException when user does not exist")
    void findUserById_NotFound_ThrowsUserNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository, times(1)).findById(userId);
    }
}
