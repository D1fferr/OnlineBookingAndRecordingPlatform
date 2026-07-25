package com.platform.booking.recording.ApiGateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final JwtParser jwtParser;

    public JwtUtil(@Value("${jwt.secret}") String jwtSecret) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
                .verifyWith(this.key)
                .build();
    }

    public Claims extractAllClaims(String token) throws JwtException {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public String extractRoles(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

//    public String extractSubject(String token) {
//        return extractAllClaims(token).getSubject();
//    }
//
//    public Date extractExpirationDate(String token) {
//        return extractAllClaims(token).getExpiration();
//    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
