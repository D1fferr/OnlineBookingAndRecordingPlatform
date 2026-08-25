package com.platform.booking.recording.AuthService.TokenProviderTests;

import com.platform.booking.recording.AuthService.dtos.TokenResponse;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.security.JwtProvider;
import com.platform.booking.recording.AuthService.security.TokenProvider;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTokensTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("createTokens: Successfully generates access and refresh tokens, saves refresh token, and returns TokenResponse")
    void createTokens_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        String expectedAccessToken = "mocked-access-token-xyz";
        String expectedRefreshToken = "mocked-refresh-token-123";

        when(jwtProvider.generateToken(user)).thenReturn(expectedAccessToken);
        when(jwtProvider.generateRefreshToken()).thenReturn(expectedRefreshToken);

        // Act
        TokenResponse response = tokenProvider.createTokens(user);

        // Assert
        assertNotNull(response);
        assertEquals(expectedAccessToken, response.getAccessToken());
        assertEquals(expectedRefreshToken, response.getRefreshToken());

        verify(jwtProvider, times(1)).generateToken(user);
        verify(jwtProvider, times(1)).generateRefreshToken();
        verify(refreshTokenService, times(1)).saveRefreshToken(userId, expectedRefreshToken);
    }
}
