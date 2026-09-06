package com.platform.booking.recording.auth_service.TokenProviderTests;


import com.platform.booking.recording.auth_service.dtos.TokenResponse;
import com.platform.booking.recording.auth_service.exceptions.UserIsBlockedException;
import com.platform.booking.recording.auth_service.models.RefreshToken;
import com.platform.booking.recording.auth_service.models.User;
import com.platform.booking.recording.auth_service.security.TokenProvider;
import com.platform.booking.recording.auth_service.services.RefreshTokenService;
import com.platform.booking.recording.auth_service.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokensTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @Spy
    @InjectMocks
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("refreshTokens: Successfully deletes old token and issues new tokens when user is active")
    void refreshTokens_Success() {
        // Arrange
        String oldRefreshTokenStr = "old-refresh-token-123";
        UUID userId = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(oldRefreshTokenStr);
        refreshToken.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setIsBlocked(Boolean.FALSE);

        TokenResponse expectedResponse = new TokenResponse("new-access-token", "new-refresh-token");

        when(refreshTokenService.findByRefreshToken(oldRefreshTokenStr)).thenReturn(refreshToken);
        when(userService.findUserById(userId)).thenReturn(user);
        doReturn(expectedResponse).when(tokenProvider).createTokens(user);

        // Act
        TokenResponse actualResponse = tokenProvider.refreshTokens(oldRefreshTokenStr);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(refreshTokenService, times(1)).delete(refreshToken);
        verify(tokenProvider, times(1)).createTokens(user);
    }

    @Test
    @DisplayName("refreshTokens: Throws UserIsBlockedException and does not delete token when user is blocked")
    void refreshTokens_UserIsBlocked_ThrowsException() {
        // Arrange
        String oldRefreshTokenStr = "old-refresh-token-123";
        UUID userId = UUID.randomUUID();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(oldRefreshTokenStr);
        refreshToken.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setIsBlocked(Boolean.TRUE);
        user.setBlockReason("Violation of terms of service");

        when(refreshTokenService.findByRefreshToken(oldRefreshTokenStr)).thenReturn(refreshToken);
        when(userService.findUserById(userId)).thenReturn(user);

        // Act & Assert
        UserIsBlockedException exception = assertThrows(
                UserIsBlockedException.class,
                () -> tokenProvider.refreshTokens(oldRefreshTokenStr)
        );

        verify(refreshTokenService, never()).delete(any());
        verify(tokenProvider, never()).createTokens(any());
    }
}
