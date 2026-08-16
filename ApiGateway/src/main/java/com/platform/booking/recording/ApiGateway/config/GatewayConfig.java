package com.platform.booking.recording.ApiGateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final ServiceUrlsConfig config;
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){

        return builder.routes()
                //provider service
                .route("provider-service", r->r
                        .path("/api/appointments/**")
                        .filters(f->f.stripPrefix(1))
                        .uri(config.getProviders()))
                .route("provider-service", r->r
                        .path("/api/providers/**")
                        .filters(f->f.stripPrefix(1))
                        .uri(config.getProviders()))
                .route("provider-service", r->r
                        .path("/api/working-hours/**")
                        .filters(f->f.stripPrefix(1))
                        .uri(config.getProviders()))
                //auth service
                .route("auth-service", r->r
                        .path("/api/user/**")
                        .filters(f->f.stripPrefix(1))
                        .uri(config.getAuth()))
                .route("auth-service", r->r
                        .path("/api/reset-password/**")
                        .filters(f->f.stripPrefix(1))
                        .uri(config.getAuth()))
                //minio
                .route("minio-images", r -> r
                        .path("/api/images/**")
                        .filters(f -> f.rewritePath(
                                "/api/images/(?<segment>.*)",
                                "/images/${segment}"
                        ))
                        .uri(config.getMinio()))
                //frontend
                .route("frontend-route", r -> r
                        .path("/**")
                        .uri(config.getFrontend()))
                .build();
    }


}
