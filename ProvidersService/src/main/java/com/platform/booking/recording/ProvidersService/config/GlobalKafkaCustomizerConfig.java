package com.platform.booking.recording.ProvidersService.config;

import com.platform.booking.recording.ProvidersService.util.KafkaTraceIdInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class GlobalKafkaCustomizerConfig {

    @Bean
    public DefaultErrorHandler errorHandler() {
        var backOff = new FixedBackOff(2000L, 2);
        return new DefaultErrorHandler(backOff);
    }
    @Bean
    public ContainerCustomizer<Object, Object, ConcurrentMessageListenerContainer<Object, Object>> traceIdContainerCustomizer(
            KafkaTraceIdInterceptor traceIdInterceptor,
            DefaultErrorHandler errorHandler) {
        return container -> {
            container.setRecordInterceptor(traceIdInterceptor);
            container.setCommonErrorHandler(errorHandler);
        };
    }
}
