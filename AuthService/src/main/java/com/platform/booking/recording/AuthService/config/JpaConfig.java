package com.platform.booking.recording.AuthService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.platform.booking.recording.AuthService.repositories.jpa")
public class JpaConfig {
}
