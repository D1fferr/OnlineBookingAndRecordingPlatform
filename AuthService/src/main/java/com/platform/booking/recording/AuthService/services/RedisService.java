package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.repositories.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisService {
    private final RefreshTokenRepository refreshTokenRepository;


    public void saveRefreshToken(String id, String token){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(UUID.fromString(id));
        refreshToken.setTtlInSeconds(604800L);                  //7days
        refreshTokenRepository.save(refreshToken);
    }
}
