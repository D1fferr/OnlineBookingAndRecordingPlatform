package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.exceptions.TokenNotFoundException;
import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(UUID id, String token){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(id);
        refreshToken.setTtlInSeconds(604800L);                  //7days
        refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new TokenNotFoundException("Refresh token not found"));
    }
    public Optional<RefreshToken> findByRefreshTokenForLogout(String refreshToken){
        return refreshTokenRepository.findByToken(refreshToken);
    }
    public void delete(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
    }
}
