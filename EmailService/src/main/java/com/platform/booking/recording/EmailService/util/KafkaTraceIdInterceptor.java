package com.platform.booking.recording.EmailService.util;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.jspecify.annotations.Nullable;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class KafkaTraceIdInterceptor implements RecordInterceptor<Object, Object> {

    private static final String TRACE_HEADER = "traceId";

    @Override
    public @Nullable ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
        String traceId = null;
        Header header = record.headers().lastHeader(TRACE_HEADER);
        if (header != null && header.value() != null) {
            traceId = new String(header.value(), StandardCharsets.UTF_8);
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(TRACE_HEADER, traceId);
        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
        MDC.remove(TRACE_HEADER);
    }
}