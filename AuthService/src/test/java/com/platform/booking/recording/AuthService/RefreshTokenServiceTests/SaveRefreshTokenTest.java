package com.platform.booking.recording.AuthService.RefreshTokenServiceTests;


import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaveRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("saveRefreshToken: Successfully creates RefreshToken entity with 7 days TTL and saves to Redis")
    void saveRefreshToken_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String token = "sample-jwt-refresh-token-string";

        // Act
        refreshTokenService.saveRefreshToken(userId, token);

        // Assert
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(1)).save(captor.capture());

        RefreshToken savedToken = captor.getValue();
        assertNotNull(savedToken);
        assertEquals(userId, savedToken.getUserId());
        assertEquals(token, savedToken.getToken());
        assertEquals(604800L, savedToken.getTtlInSeconds());
    }
}