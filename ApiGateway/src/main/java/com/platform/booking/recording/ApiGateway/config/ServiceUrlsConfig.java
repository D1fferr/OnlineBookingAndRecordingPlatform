package com.platform.booking.recording.ApiGateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services")
@Data
public class ServiceUrlsConfig {
    private String providers;
    private String auth;
    private String frontend;
    private String minio;
}
