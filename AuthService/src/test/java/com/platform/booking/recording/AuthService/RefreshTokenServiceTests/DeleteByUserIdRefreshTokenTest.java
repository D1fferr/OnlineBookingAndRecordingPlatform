package com.platform.booking.recording.AuthService.RefreshTokenServiceTests;


import com.platform.booking.recording.AuthService.dtos.UserIdDTO;
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
class DeleteByUserIdRefreshTokenTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("deleteByUserId: Successfully invokes refreshTokenRepository.deleteByUserId with ID from DTO")
    void deleteByUserId_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserIdDTO dto = new UserIdDTO(userId);

        // Act
        refreshTokenService.deleteByUserId(dto);

        // Assert
        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
    }
}