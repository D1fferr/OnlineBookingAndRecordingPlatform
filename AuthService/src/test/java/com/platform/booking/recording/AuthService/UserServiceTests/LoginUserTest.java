package com.platform.booking.recording.AuthService.UserServiceTests;

import com.platform.booking.recording.AuthService.dtos.LoginDTO;
import com.platform.booking.recording.AuthService.exceptions.BadCredentialsException;
import com.platform.booking.recording.AuthService.exceptions.UserIsBlockedException;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.services.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("login: Successfully authenticates non-blocked user with valid credentials")
    void login_Success() {
        // Arrange
        String email = "user@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsBlocked(Boolean.FALSE);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act
        User loggedInUser = userService.login(dto);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(user, loggedInUser);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("login: Throws BadCredentialsException when email is not found")
    void login_EmailNotFound_ThrowsBadCredentialsException() {
        // Arrange
        LoginDTO dto = new LoginDTO();
        dto.setEmail("unknown@example.com");
        dto.setPassword("password123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userService.login(dto));

        verify(userRepository, times(1)).findByEmail(dto.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("login: Throws BadCredentialsException when password does not match")
    void login_InvalidPassword_ThrowsBadCredentialsException() {
        // Arrange
        String email = "user@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userService.login(dto));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("login: Throws UserIsBlockedException when user account is blocked")
    void login_UserIsBlocked_ThrowsUserIsBlockedException() {
        // Arrange
        String email = "blocked@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        String blockReason = "Violation of terms";

        LoginDTO dto = new LoginDTO();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsBlocked(Boolean.TRUE);
        user.setBlockReason(blockReason);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Act & Assert
        UserIsBlockedException exception = assertThrows(
                UserIsBlockedException.class,
                () -> userService.login(dto)
        );

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }
}