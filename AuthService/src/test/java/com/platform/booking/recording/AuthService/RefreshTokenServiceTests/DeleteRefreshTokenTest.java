package com.platform.booking.recording.AuthService.RefreshTokenServiceTests;


import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("delete: Successfully calls refreshTokenRepository.delete with given RefreshToken entity")
    void delete_Success() {
        // Arrange
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("sample-refresh-token");
        refreshToken.setUserId(UUID.randomUUID());
        refreshToken.setTtlInSeconds(604800L);

        // Act
        refreshTokenService.delete(refreshToken);

        // Assert
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }
}