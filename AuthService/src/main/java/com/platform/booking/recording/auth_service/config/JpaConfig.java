package com.platform.booking.recording.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.platform.booking.recording.auth_service.repositories.jpa")
public class JpaConfig {
}
