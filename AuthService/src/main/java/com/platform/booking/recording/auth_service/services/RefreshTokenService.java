package com.platform.booking.recording.auth_service.services;

import com.platform.booking.recording.auth_service.dtos.UserIdDTO;
import com.platform.booking.recording.auth_service.exceptions.TokenNotFoundException;
import com.platform.booking.recording.auth_service.models.RefreshToken;
import com.platform.booking.recording.auth_service.repositories.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(UUID id, String token){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(id);
        refreshToken.setTtlInSeconds(604800L);                  //7days
        refreshTokenRepository.save(refreshToken);
        log.atInfo()
                .addKeyValue("userId", id)
                .log("The refresh token saved");
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
        log.atInfo()
                .addKeyValue("userId", refreshToken.getUserId())
                .log("The refresh token deleted by entity");
    }
    @Order(1)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteByUserId(UserIdDTO dto){
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(dto.getId());
        if (refreshToken.isEmpty()){
            log.atInfo()
                    .addKeyValue("userId", dto.getId())
                    .log("The refresh token not found by user id");
            return;
        }
        refreshTokenRepository.delete(refreshToken.get());
        log.atInfo()
                .addKeyValue("userId", dto.getId())
                .log("The refresh token deleted by user id");
    }
}
