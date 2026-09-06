package com.platform.booking.recording.auth_service.UserServiceTests;

import com.platform.booking.recording.auth_service.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.auth_service.exceptions.UserNotFoundException;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.jpa.UserRepository;
import com.platform.booking.recording.auth_service.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnblockUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("unblockUser: Successfully sets isBlocked to false, saves user, publishes event, and updates MDC")
    void unblockUser_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setIsBlocked(Boolean.TRUE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.unblockUser(userId);

        // Assert
        assertEquals(userId.toString(), MDC.get("userId"));
        assertFalse(user.getIsBlocked());

        verify(userRepository, times(1)).save(user);

        ArgumentCaptor<ProviderIsBlockedDTO> eventCaptor = ArgumentCaptor.forClass(ProviderIsBlockedDTO.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        ProviderIsBlockedDTO publishedEvent = eventCaptor.getValue();
        assertEquals(userId, publishedEvent.getId());
        assertFalse(publishedEvent.getIsBlocked());
    }

    @Test
    @DisplayName("unblockUser: Throws UserNotFoundException when user does not exist")
    void unblockUser_UserNotFound_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.unblockUser(userId));

        assertEquals(userId.toString(), MDC.get("userId"));
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}