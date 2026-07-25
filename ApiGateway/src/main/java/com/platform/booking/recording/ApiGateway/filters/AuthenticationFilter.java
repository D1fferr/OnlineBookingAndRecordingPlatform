package com.platform.booking.recording.ApiGateway.filters;

import com.platform.booking.recording.ApiGateway.config.SecurityProperties;
import com.platform.booking.recording.ApiGateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isPublicRoute(path)) {
            log.debug("Public route accessed: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing JWT token", "AUTH_REQUIRED");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token attempt for path: {}", path);
            return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token", "INVALID_TOKEN");
        }

        if (!hasPermission(token, path)) {
            log.warn("Forbidden access attempt to path: {} with insufficient roles", path);
            return buildErrorResponse(exchange, HttpStatus.FORBIDDEN, "Insufficient permissions for this resource", "INSUFFICIENT_PERMISSIONS");
        }
        return chain.filter(exchange);
    }

    private boolean isPublicRoute(String path) {
        return securityProperties.getPublicRoutes().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private boolean hasPermission(String token, String path) {
        Map<String, Set<String>> rolePermissions = securityProperties.getRolePermissions();

        Set<String> requiredRoles = rolePermissions.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), path))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }

        String userRole = jwtUtil.extractRoles(token);
        if (userRole == null || userRole.isBlank()) {
            return false;
        }
        return requiredRoles.contains(userRole);
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, String message, String errorCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = String.format(
                "{\"error\": \"%s\", \"message\": \"%s\", \"code\": \"%s\"}",
                status.getReasonPhrase().toLowerCase(), message, errorCode
        );

        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
