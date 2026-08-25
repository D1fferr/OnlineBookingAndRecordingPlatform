package com.platform.booking.recording.ApiGateway.AuthenticationFilterTests;

import com.platform.booking.recording.ApiGateway.config.SecurityProperties;
import com.platform.booking.recording.ApiGateway.filters.AuthenticationFilter;
import com.platform.booking.recording.ApiGateway.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityProperties securityProperties;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    private static final String TRACE_HEADER = "X-Trace-Id";


    @Test
    @DisplayName("filter: External route (not starting with /api) passes without token")
    void filter_ExternalRoute_PassesFilter() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(exchange);
        assertNotNull(exchange.getResponse().getHeaders().getFirst(TRACE_HEADER));
    }

    @Test
    @DisplayName("filter: Public API route passes without token")
    void filter_PublicRoute_PassesFilter() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(securityProperties.getPublicRoutes()).thenReturn(List.of("/api/v1/auth/**"));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("filter: Missing Authorization header returns 401 Unauthorized")
    void filter_MissingAuthHeader_Returns401() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/protected/resource").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(securityProperties.getPublicRoutes()).thenReturn(List.of("/api/v1/auth/**"));

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());
        verify(filterChain, never()).filter(any());
    }

    @Test
    @DisplayName("filter: Invalid JWT token returns 401 Unauthorized")
    void filter_InvalidToken_Returns401() {
        // Arrange
        String token = "invalid-token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(securityProperties.getPublicRoutes()).thenReturn(List.of());
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(jwtUtil, times(1)).validateToken(token);
        verify(filterChain, never()).filter(any());
    }

    @Test
    @DisplayName("filter: Insufficient user role permissions returns 403 Forbidden")
    void filter_InsufficientPermissions_Returns403() {
        // Arrange
        String token = "valid-user-token";
        String path = "/api/v1/admin/dashboard";

        MockServerHttpRequest request = MockServerHttpRequest.get(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(securityProperties.getPublicRoutes()).thenReturn(List.of());
        when(securityProperties.getRolePermissions()).thenReturn(Map.of("/api/v1/admin/**", Set.of("ROLE_ADMIN")));
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractRoles(token)).thenReturn("ROLE_USER");

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        verify(filterChain, never()).filter(any());
    }

    @Test
    @DisplayName("filter: Valid token and matching role passes filter successfully")
    void filter_ValidTokenAndRole_PassesFilter() {
        // Arrange
        String token = "valid-admin-token";
        String path = "/api/v1/admin/dashboard";
        String traceId = "custom-trace-123";

        MockServerHttpRequest request = MockServerHttpRequest.get(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TRACE_HEADER, traceId)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(securityProperties.getPublicRoutes()).thenReturn(List.of());
        when(securityProperties.getRolePermissions()).thenReturn(Map.of("/api/v1/admin/**", Set.of("ROLE_ADMIN")));
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractRoles(token)).thenReturn("ROLE_ADMIN");
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = authenticationFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(any(ServerWebExchange.class));
        assertEquals(traceId, exchange.getResponse().getHeaders().getFirst(TRACE_HEADER));
    }

    @Test
    @DisplayName("getOrder: Returns Ordered.HIGHEST_PRECEDENCE")
    void getOrder_ReturnsHighestPrecedence() {
        assertEquals(Integer.MIN_VALUE, authenticationFilter.getOrder());
    }
}