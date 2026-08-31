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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserByEmailTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("findUserByEmail: Successfully returns Optional containing user when email exists")
    void findUserByEmail_UserFound() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findUserByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        assertEquals(email, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("findUserByEmail: Returns Optional.empty when email does not exist")
    void findUserByEmail_NotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findUserByEmail(email);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
