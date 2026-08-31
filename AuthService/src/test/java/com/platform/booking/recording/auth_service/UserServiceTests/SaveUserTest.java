package com.platform.booking.recording.auth_service.UserServiceTests;


import com.platform.booking.recording.auth_service.dtos.RegistrationUserDTO;
import com.platform.booking.recording.auth_service.dtos.UserForKafkaDTO;
import com.platform.booking.recording.auth_service.exceptions.FailedSaveImageException;
import com.platform.booking.recording.auth_service.exceptions.UserAlreadyExistException;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.repositories.jpa.UserRepository;
import com.platform.booking.recording.auth_service.services.ImageService;
import com.platform.booking.recording.auth_service.services.UserService;
import com.platform.booking.recording.auth_service.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Mapper mapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MultipartFile file;
    @Mock
    private ImageService imageService;

    @Spy
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("save: Successfully saves user, sets avatar URL and updates user via userRepository.save")
    void save_Success_WithFile() throws Exception {
        // Arrange
        String rawPassword = "rawPassword123";
        String encodedPassword = "encodedPassword123";
        String email = "test@example.com";
        String avatarUrl = "http://image-url.com/avatar.png";
        UUID userId = UUID.randomUUID();

        RegistrationUserDTO dto = new RegistrationUserDTO();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        UserForKafkaDTO kafkaDTO = new UserForKafkaDTO();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(mapper.registrationUserToUser(dto)).thenReturn(user);
        when(mapper.registrationUserToUserToKafkaDTO(dto, user)).thenReturn(kafkaDTO);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        when(imageService.storeImage(file, userId)).thenReturn(avatarUrl);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.save(dto, file);

        // Assert
        assertNotNull(result);
        assertEquals(encodedPassword, dto.getPassword());
        assertEquals("ROLE_PROVIDER", user.getRole());
        assertEquals(Boolean.FALSE, user.getIsBlocked());
        assertEquals(avatarUrl, user.getAvatarURL());
        assertNotNull(user.getCreatedAt());

        verify(userRepository, times(1)).saveAndFlush(user);
        verify(imageService, times(1)).storeImage(file, userId);
        verify(userRepository, times(1)).save(user);
        verify(eventPublisher, times(1)).publishEvent(kafkaDTO);
    }

    @Test
    @DisplayName("save: Successfully saves user when file is null")
    void save_Success_NullFile() throws Exception {
        // Arrange
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();

        RegistrationUserDTO dto = new RegistrationUserDTO();
        dto.setEmail(email);
        dto.setPassword("password");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(mapper.registrationUserToUser(dto)).thenReturn(user);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.save(dto, null);

        // Assert
        assertNotNull(result);
        verify(imageService, never()).storeImage(any(), any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("save: Throws FailedSaveImageException when ImageService fails")
    void save_ImageServiceFails_ThrowsFailedSaveImageException() throws Exception {
        // Arrange
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();

        RegistrationUserDTO dto = new RegistrationUserDTO();
        dto.setEmail(email);
        dto.setPassword("password");

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(mapper.registrationUserToUser(dto)).thenReturn(user);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        when(imageService.storeImage(file, userId)).thenThrow(new RuntimeException("S3 upload error"));

        // Act & Assert
        assertThrows(FailedSaveImageException.class, () -> userService.save(dto, file));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("save: Throws UserAlreadyExistException when email is already registered")
    void save_UserAlreadyExists_ThrowsException() throws Exception {
        // Arrange
        String email = "existing@example.com";
        RegistrationUserDTO dto = new RegistrationUserDTO();
        dto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistException.class, () -> userService.save(dto, file));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).saveAndFlush(any());
        verify(imageService, never()).storeImage(any(), any());
    }
}