package com.platform.booking.recording.auth_service.RefreshTokenServiceTests;


import com.platform.booking.recording.auth_service.exceptions.TokenNotFoundException;
import com.platform.booking.recording.auth_service.models.RefreshToken;
import com.platform.booking.recording.auth_service.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.auth_service.services.RefreshTokenService;
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
class FindByRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("findByRefreshToken: Successfully returns RefreshToken when found in Redis")
    void findByRefreshToken_Success() {
        // Arrange
        String tokenStr = "valid-refresh-token-123";
        UUID userId = UUID.randomUUID();

        RefreshToken expectedRefreshToken = new RefreshToken();
        expectedRefreshToken.setToken(tokenStr);
        expectedRefreshToken.setUserId(userId);
        expectedRefreshToken.setTtlInSeconds(604800L);

        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(expectedRefreshToken));

        // Act
        RefreshToken actualRefreshToken = refreshTokenService.findByRefreshToken(tokenStr);

        // Assert
        assertNotNull(actualRefreshToken);
        assertEquals(expectedRefreshToken, actualRefreshToken);
        assertEquals(tokenStr, actualRefreshToken.getToken());
        assertEquals(userId, actualRefreshToken.getUserId());

        verify(refreshTokenRepository, times(1)).findByToken(tokenStr);
    }

    @Test
    @DisplayName("findByRefreshToken: Throws TokenNotFoundException when token is not found")
    void findByRefreshToken_NotFound_ThrowsTokenNotFoundException() {
        // Arrange
        String tokenStr = "invalid-or-expired-token";

        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TokenNotFoundException.class, () -> refreshTokenService.findByRefreshToken(tokenStr));

        verify(refreshTokenRepository, times(1)).findByToken(tokenStr);
    }
}