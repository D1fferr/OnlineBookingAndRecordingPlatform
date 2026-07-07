package com.platform.booking.recording.AuthService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.platform.booking.recording.AuthService.repositories.redis")
public class RedisConfig {
}
