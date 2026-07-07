package com.platform.booking.recording.AuthSevice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.platform.booking.recording.AuthSevice.repositories.jpa")
public class JpaConfig {
}
