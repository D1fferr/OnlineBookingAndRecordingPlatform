package com.platform.booking.recording.email_service.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsCounter {
    private final MeterRegistry meterRegistry;
    public void incrementEmailCounter(String templateType, String status) {
        Counter.builder("emails.sent.total")
                .description("Total number of sent emails")
                .tag("type", templateType)
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }
}
