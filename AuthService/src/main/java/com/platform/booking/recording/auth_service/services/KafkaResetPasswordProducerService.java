package com.platform.booking.recording.auth_service.services;

import com.platform.booking.recording.auth_service.models.ResetPassword;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaResetPasswordProducerService {

    private final KafkaTemplate<String, ResetPassword> userResetPasswordKafkaTemplate;
    private static final String TRACE_ID_KEY = "traceId";

    public void send(ResetPassword resetPassword){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<ResetPassword> message = MessageBuilder
                .withPayload(resetPassword)
                .setHeader(KafkaHeaders.TOPIC, "reset-password-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userResetPasswordKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }



}
