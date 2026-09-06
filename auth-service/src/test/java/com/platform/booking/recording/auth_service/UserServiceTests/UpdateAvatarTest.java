package com.platform.booking.recording.auth_service.UserServiceTests;


import com.platform.booking.recording.auth_service.dtos.UserAvatarForKafkaDTO;
import com.platform.booking.recording.auth_service.exceptions.FailedSaveImageException;
import com.platform.booking.recording.auth_service.exceptions.UserNotFoundException;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.jpa.UserRepository;
import com.platform.booking.recording.auth_service.services.ImageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAvatarTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("updateAvatar: Successfully updates avatar URL, saves user, publishes event and sets MDC")
    void updateAvatar_Success() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String expectedAvatarUrl = "http://minio-s3.com/avatars/new-avatar.png";

        User user = new User();
        user.setId(userId);
        user.setAvatarURL("http://minio-s3.com/avatars/old-avatar.png");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(imageService.storeImage(file, userId)).thenReturn(expectedAvatarUrl);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateAvatar(userId, file);

        // Assert
        assertEquals(userId.toString(), MDC.get("providerId"));
        assertEquals(expectedAvatarUrl, user.getAvatarURL());

        verify(userRepository, times(1)).save(user);

        ArgumentCaptor<UserAvatarForKafkaDTO> eventCaptor = ArgumentCaptor.forClass(UserAvatarForKafkaDTO.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        UserAvatarForKafkaDTO publishedEvent = eventCaptor.getValue();
        assertEquals(userId, publishedEvent.getId());
        assertEquals(expectedAvatarUrl, publishedEvent.getAvatarURL());
    }

    @Test
    @DisplayName("updateAvatar: Throws FailedSaveImageException when ImageService fails")
    void updateAvatar_ImageServiceFailure_ThrowsFailedSaveImageException() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(imageService.storeImage(file, userId)).thenThrow(new RuntimeException("Storage unavailable"));

        // Act & Assert
        assertThrows(FailedSaveImageException.class, () -> userService.updateAvatar(userId, file));

        assertEquals(userId.toString(), MDC.get("providerId"));
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("updateAvatar: Throws UserNotFoundException when user does not exist")
    void updateAvatar_UserNotFound_ThrowsException() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateAvatar(userId, file));

        assertEquals(userId.toString(), MDC.get("providerId"));
        verify(imageService, never()).storeImage(any(), any());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
