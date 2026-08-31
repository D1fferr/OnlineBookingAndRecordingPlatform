package com.platform.booking.recording.api_gateway;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.platform.booking.recording.api_gateway.config.SecurityProperties;
import com.platform.booking.recording.api_gateway.security.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {
    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void initWebTestClient() {
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }
    private static WireMockServer wireMockServer;



    @Autowired
    private SecurityProperties securityProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void overrideServiceProperties(DynamicPropertyRegistry registry) {
        String wiremockUrl = "http://localhost:" + wireMockServer.port();
        registry.add("services.providers", () -> wiremockUrl);
        registry.add("services.auth", () -> wiremockUrl);
        registry.add("services.minio", () -> wiremockUrl);
        registry.add("services.frontend", () -> wiremockUrl);
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();

        // Configure security rules for test execution
        securityProperties.setPublicRoutes(List.of("/api/user/login", "/api/user/register", "/api/images/avatar.jpg"));
        securityProperties.setRolePermissions(Map.of(
                "/api/providers/**", Set.of("ROLE_ADMIN", "ROLE_PROVIDER")
        ));
    }

    @Test
    @DisplayName("Routing: /api/providers/list strips prefix and routes to WireMock with X-Trace-Id header")
    void shouldStripPrefixAndRouteToProviderService_WhenAuthenticated() {
        // Arrange
        String token = "valid-admin-token";
        wireMockServer.stubFor(get(urlEqualTo("/providers/list"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"status\":\"success\"}")));

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractRoles(token)).thenReturn("ROLE_ADMIN");

        // Act & Assert
        webTestClient.get()
                .uri("/api/providers/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Trace-Id")
                .expectBody()
                .jsonPath("$.status").isEqualTo("success");

        // Verify that Gateway forwarded request with stripped path '/providers/list'
        wireMockServer.verify(getRequestedFor(urlEqualTo("/providers/list"))
                .withHeader("X-Trace-Id", WireMock.matching(".+")));
    }

    @Test
    @DisplayName("Routing: /api/images/avatar.jpg rewrites path to /images/avatar.jpg for MinIO")
    void shouldRewritePathForMinioImages() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/images/avatar.jpg"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)));

        // Act & Assert
        webTestClient.get()
                .uri("/api/images/avatar.jpg")
                .exchange()
                .expectStatus().isOk();

        wireMockServer.verify(getRequestedFor(urlEqualTo("/images/avatar.jpg")));
    }

    @Test
    @DisplayName("AuthFilter: Bypasses authentication for configured public routes")
    void shouldAllowPublicRoutesWithoutAuthHeader() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/user/login"))
                .willReturn(aResponse().withStatus(200)));

        // Act & Assert
        webTestClient.get()
                .uri("/api/user/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("AuthFilter: Returns 401 UNAUTHORIZED with JSON payload when JWT header is missing")
    void shouldReturn401_WhenAuthHeaderIsMissingOnProtectedRoute() {
        webTestClient.get()
                .uri("/api/providers/list")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo("AUTH_REQUIRED")
                .jsonPath("$.message").isEqualTo("Missing JWT token");
    }

    @Test
    @DisplayName("AuthFilter: Returns 403 FORBIDDEN when user role does not match required path permissions")
    void shouldReturn403_WhenUserHasInsufficientPermissions() {
        // Arrange
        String token = "valid-client-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractRoles(token)).thenReturn("ROLE_CLIENT");

        // Act & Assert
        webTestClient.get()
                .uri("/api/providers/list")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INSUFFICIENT_PERMISSIONS");
    }
}