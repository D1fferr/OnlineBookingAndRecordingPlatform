package com.platform.booking.recording.AuthService.repositories;

import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import com.platform.booking.recording.AuthService.services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefreshTokenRepositoryTest extends AbstractBaseRedisTest {
    @MockitoBean
    private ImageService imageService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private UUID userId1;
    private UUID userId2;
    private String tokenStr1;
    private String tokenStr2;

    @BeforeEach
    void setUp() {
        // Clean Redis storage before each test execution
        refreshTokenRepository.deleteAll();

        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        tokenStr1 = "sample-jwt-refresh-token-1";
        tokenStr2 = "sample-jwt-refresh-token-2";

        // Create and save test refresh tokens
        RefreshToken token1 = createRefreshToken(tokenStr1, userId1, 3600L);
        RefreshToken token2 = createRefreshToken(tokenStr2, userId2, 3600L);

        refreshTokenRepository.save(token1);
        refreshTokenRepository.save(token2);
    }

    private RefreshToken createRefreshToken(String token, UUID userId, Long ttlInSeconds) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setTtlInSeconds(ttlInSeconds);
        return refreshToken;
    }

    @Test
    @DisplayName("findByToken: Retrieves RefreshToken successfully when token string exists")
    void findByToken_ReturnsRefreshToken() {
        // Act
        Optional<RefreshToken> result = refreshTokenRepository.findByToken(tokenStr1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(tokenStr1);
        assertThat(result.get().getUserId()).isEqualTo(userId1);
        assertThat(result.get().getTtlInSeconds()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("findByToken: Returns empty Optional when token does not exist in Redis")
    void findByToken_ReturnsEmpty_WhenNotFound() {
        // Act
        Optional<RefreshToken> result = refreshTokenRepository.findByToken("non-existing-token");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteByUserId: Removes refresh token associated with specified userId")
    void deleteByUserId_RemovesTokenSuccessfully() {
        Optional<RefreshToken> tokenToDelete = refreshTokenRepository.findByUserId(userId1);
        assertThat(tokenToDelete).isPresent();
        refreshTokenRepository.delete(tokenToDelete.get());

        // Assert
        Optional<RefreshToken> deletedToken = refreshTokenRepository.findByToken(tokenStr1);
        Optional<RefreshToken> remainingToken = refreshTokenRepository.findByToken(tokenStr2);

        assertThat(deletedToken).isEmpty();
        assertThat(remainingToken).isPresent();
    }
}
