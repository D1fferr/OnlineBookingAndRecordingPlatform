package com.platform.booking.recording.AuthService.security;

import com.platform.booking.recording.AuthService.dtos.TokenResponse;
import com.platform.booking.recording.AuthService.exceptions.UserIsBlockedException;
import com.platform.booking.recording.AuthService.models.RefreshToken;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.services.RefreshTokenService;
import com.platform.booking.recording.AuthService.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenResponse refreshTokens(String refreshTokenOld){
        RefreshToken refreshToken = refreshTokenService.findByRefreshToken(refreshTokenOld);
        User user = userService.findUserById(refreshToken.getUserId());
        if (user.getIsBlocked())
            throw new UserIsBlockedException(user.getBlockReason());
        refreshTokenService.delete(refreshToken);
        return createTokens(user);
    }
    public TokenResponse createTokens(User user){
        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = jwtProvider.generateRefreshToken();
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }
    public ResponseCookie createResponseCookie(String refreshToken){
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)                             //xss protection
                .secure(false)                              //http
                .path("/api/user/public/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")                         //csrf protection
                .build();
    }
    public ResponseCookie createClearShareCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/user/public/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

}
