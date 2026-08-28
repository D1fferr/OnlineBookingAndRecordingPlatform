package com.platform.booking.recording.AuthService.RefreshTokenServiceTests;


import com.platform.booking.recording.AuthService.dtos.UserIdDTO;
import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteByUserIdRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("deleteByUserId: Finds token by userId and deletes entity when present")
    void deleteByUserId_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserIdDTO dto = new UserIdDTO(userId);
        RefreshToken existingToken = new RefreshToken("token-123", userId, 3600L);

        when(refreshTokenRepository.findByUserId(userId))
                .thenReturn(Optional.of(existingToken));

        // Act
        refreshTokenService.deleteByUserId(dto);

        // Assert
        verify(refreshTokenRepository).findByUserId(userId);
        verify(refreshTokenRepository).delete(existingToken);
    }

    @Test
    @DisplayName("deleteByUserId: Does nothing when token for userId is not found")
    void deleteByUserId_WhenTokenNotFound_DoesNotDelete() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserIdDTO dto = new UserIdDTO(userId);

        when(refreshTokenRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        // Act
        refreshTokenService.deleteByUserId(dto);

        // Assert
        verify(refreshTokenRepository).findByUserId(userId);
        verify(refreshTokenRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }
}