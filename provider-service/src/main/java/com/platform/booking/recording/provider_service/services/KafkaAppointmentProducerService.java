package com.platform.booking.recording.provider_service.services;

import com.platform.booking.recording.provider_service.dtos.KafkaDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAppointmentProducerService {

    private final KafkaTemplate<String, AppointmentForKafkaDTO> appointmentCreateKafkaTemplate;
    private final KafkaTemplate<String, AppointmentCancelledForKafkaDTO> appointmentCancelledKafkaTemplate;
    private final AppointmentService appointmentService;
    private static final String TRACE_ID_KEY = "traceId";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToCreate(AppointmentCreateForKafkaDTO appointment){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentCreateForKafkaDTO> message = MessageBuilder
                .withPayload(appointment)
                .setHeader(KafkaHeaders.TOPIC, "appointment-create-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getSecureToken());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToConfirmed(AppointmentConfirmedForKafkaDTO appointment){

        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentConfirmedForKafkaDTO> message = MessageBuilder
                .withPayload(appointment)
                .setHeader(KafkaHeaders.TOPIC, "appointment-confirmed-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getSecureToken());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToCancelled(AppointmentCancelledForKafkaDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentCancelledForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-cancelled-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCancelledKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(dto.getSecureToken());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDeleted(AppointmentDeletedForKafkaDTO dto){
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentDeletedForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-deleted-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }

}
