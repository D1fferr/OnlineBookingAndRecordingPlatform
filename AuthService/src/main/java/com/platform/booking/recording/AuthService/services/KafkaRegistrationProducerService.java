package com.platform.booking.recording.AuthService.services;

import com.platform.booking.recording.AuthService.dtos.ProviderIsBlockedDTO;
import com.platform.booking.recording.AuthService.dtos.ProviderUpdateEmailDTO;
import com.platform.booking.recording.AuthService.dtos.RegistrationUserDTO;
import com.platform.booking.recording.AuthService.dtos.UserForKafkaDTO;
import com.platform.booking.recording.AuthService.models.User;
import com.platform.booking.recording.AuthService.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class KafkaRegistrationProducerService {
    private final KafkaTemplate<String, UserForKafkaDTO> userRegistrationKafkaTemplate;
    private final KafkaTemplate<String, ProviderUpdateEmailDTO> userEmailKafkaTemplate;
    private final KafkaTemplate<String, ProviderIsBlockedDTO> userIsBlockedKafkaTemplate;
    private final Mapper mapper;
    private static final String TRACE_ID_KEY = "traceId";

    public void send(RegistrationUserDTO dto, User user){
        UserForKafkaDTO userForKafkaDTO = mapper.registrationUserToUserToKafkaDTO(dto, user);
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<UserForKafkaDTO> message = MessageBuilder
                .withPayload(userForKafkaDTO)
                .setHeader(KafkaHeaders.TOPIC, "user-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            userRegistrationKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }

    }
    public void sendEmail(UUID id, String email){
        ProviderUpdateEmailDTO dto = new ProviderUpdateEmailDTO(id, email);
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
    public void sendIsBlocked(UUID id, Boolean isBlocked){
        ProviderIsBlockedDTO dto = new ProviderIsBlockedDTO(id, isBlocked);
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

}
