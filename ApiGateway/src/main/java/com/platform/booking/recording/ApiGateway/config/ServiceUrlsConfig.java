package library.com.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services")
@Data
public class ServiceUrlsConfig {
    private String catalog;
    private String reservation;
    private String expectedBook;
    private String event;
    private String user;
    private String auth;
    private String frontend;
    private String minio;
}
