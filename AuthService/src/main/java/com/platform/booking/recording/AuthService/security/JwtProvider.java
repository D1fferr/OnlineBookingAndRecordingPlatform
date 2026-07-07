package com.platform.booking.recording.AuthService.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.platform.booking.recording.AuthService.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    public String generateToken(User user) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(15).toInstant());
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("user_id", user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole())
                .withIssuedAt(new Date())
                .withIssuer("AuthService")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(jwtSecretKey));
    }
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
