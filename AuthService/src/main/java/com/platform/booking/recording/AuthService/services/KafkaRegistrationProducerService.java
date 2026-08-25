package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.*;
import com.platform.booking.recording.AuthService.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class KafkaRegistrationProducerService {
    private final KafkaTemplate<String, UserForKafkaDTO> userRegistrationKafkaTemplate;
    private final KafkaTemplate<String, ProviderUpdateEmailDTO> userEmailKafkaTemplate;
    private final KafkaTemplate<String, ProviderIsBlockedDTO> userIsBlockedKafkaTemplate;
    private final KafkaTemplate<String, ProviderIsBlockedDTO> userAvatarKafkaTemplate;
    private static final String TRACE_ID_KEY = "traceId";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(UserForKafkaDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<UserForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "user-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userRegistrationKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }

    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendEmail(ProviderUpdateEmailDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<ProviderUpdateEmailDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "user-email-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userEmailKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }

    }
    @Order(2)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendIsBlocked(ProviderIsBlockedDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<ProviderIsBlockedDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "user-is-blocked-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userIsBlockedKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }

    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAvatar(UserAvatarForKafkaDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<UserAvatarForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "user-avatar-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userAvatarKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }

    }

}
