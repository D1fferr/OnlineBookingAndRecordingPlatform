package com.platform.booking.recording.AuthService.UserServiceTests;

import com.platform.booking.recording.AuthService.dtos.BlockUserDTO;
import com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.AuthService.dtos.UserIdDTO;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("blockUser: Successfully blocks user, saves, and publishes events")
    void blockUser_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String reason = "Violation of platform rules";

        BlockUserDTO dto = new BlockUserDTO();
        dto.setUserId(userId);
        dto.setReason(reason);

        User user = new User();
        user.setId(userId);
        user.setIsBlocked(Boolean.FALSE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.blockUser(dto);

        // Assert
        assertTrue(user.getIsBlocked());
        assertEquals(reason, user.getBlockReason());
        verify(userRepository, times(1)).save(user);

        // Verify that events were published
        verify(eventPublisher, times(1)).publishEvent(any(ProviderIsBlockedDTO.class));
        verify(eventPublisher, times(1)).publishEvent(any(UserIdDTO.class));
    }

    @Test
    @DisplayName("blockUser: Throws UserNotFoundException when user does not exist")
    void blockUser_UserNotFound_ThrowsException() {
        // Arrange
        BlockUserDTO dto = new BlockUserDTO();
        dto.setUserId(UUID.randomUUID());
        dto.setReason("Spam");

        when(userRepository.findById(dto.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.blockUser(dto));

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
