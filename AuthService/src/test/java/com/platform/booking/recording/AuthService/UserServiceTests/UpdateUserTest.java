package com.platform.booking.recording.AuthService.UserServiceTests;

import com.platform.booking.recording.AuthService.dtos.ChangeCredentialsDTO;
import com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.AuthService.exceptions.BadCredentialsException;
import com.platform.booking.recording.AuthService.exceptions.UserNotFoundException;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.repositories.jpa.UserRepository;
import com.platform.booking.recording.AuthService.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("updateUser: Successfully updates email and password, publishes event and saves user")
    void updateUser_Success_EmailAndPassword() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String currentPassword = "oldPassword123";
        String encodedCurrentPassword = "encodedOldPassword";
        String newPassword = "newPassword456";
        String encodedNewPassword = "encodedNewPassword";
        String newEmail = "new.email@example.com";

        ChangeCredentialsDTO dto = new ChangeCredentialsDTO();
        dto.setCurrentPassword(currentPassword);
        dto.setPassword(newPassword);
        dto.setEmail(newEmail);

        User user = new User();
        user.setId(userId);
        user.setEmail("old.email@example.com");
        user.setPassword(encodedCurrentPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = userService.updateUser(userId, dto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(encodedNewPassword, updatedUser.getPassword());

        verify(eventPublisher, times(1)).publishEvent(any(ProviderUpdateEmailDTO.class));
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("updateUser: Updates email only when new password is null")
    void updateUser_Success_EmailOnly() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String currentPassword = "oldPassword123";
        String encodedCurrentPassword = "encodedOldPassword";
        String newEmail = "new.email@example.com";

        ChangeCredentialsDTO dto = new ChangeCredentialsDTO();
        dto.setCurrentPassword(currentPassword);
        dto.setEmail(newEmail);
        dto.setPassword(null);

        User user = new User();
        user.setId(userId);
        user.setEmail("old.email@example.com");
        user.setPassword(encodedCurrentPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = userService.updateUser(userId, dto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(encodedCurrentPassword, updatedUser.getPassword());

        verify(eventPublisher, times(1)).publishEvent(any(ProviderUpdateEmailDTO.class));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("updateUser: Throws BadCredentialsException when current password is invalid")
    void updateUser_InvalidCurrentPassword_ThrowsBadCredentialsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String wrongPassword = "wrongPassword";
        String encodedCurrentPassword = "encodedOldPassword";

        ChangeCredentialsDTO dto = new ChangeCredentialsDTO();
        dto.setCurrentPassword(wrongPassword);

        User user = new User();
        user.setId(userId);
        user.setPassword(encodedCurrentPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPassword, encodedCurrentPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userService.updateUser(userId, dto));

        verify(eventPublisher, never()).publishEvent(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser: Throws UserNotFoundException when user id does not exist")
    void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChangeCredentialsDTO dto = new ChangeCredentialsDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, dto));

        verify(passwordEncoder, never()).matches(any(), any());
        verify(userRepository, never()).save(any());
    }
}
