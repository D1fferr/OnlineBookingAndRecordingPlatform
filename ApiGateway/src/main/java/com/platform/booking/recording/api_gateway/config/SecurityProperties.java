package com.platform.booking.recording.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private List<String> publicRoutes = new ArrayList<>();
    private Map<String, Set<String>> rolePermissions = new HashMap<>();
}
