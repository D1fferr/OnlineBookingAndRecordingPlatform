package com.platform.booking.recording.AuthService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external")
@Data
public class ExternalConfig {

    private Kafka kafka = new Kafka();

    @Data
    public static class Kafka {
        private String endpoint;
    }

}
