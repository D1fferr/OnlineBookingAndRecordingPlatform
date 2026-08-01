package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledReasonDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAppointmentProducerService {

    private final KafkaTemplate<String, AppointmentForKafkaDTO> appointmentCreateKafkaTemplate;
    private final KafkaTemplate<String, AppointmentCancelledForKafkaDTO> appointmentCancelledKafkaTemplate;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentService appointmentService;
    private static final String TRACE_ID_KEY = "traceId";

    public void sendToCreate(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-create-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToConfirmed(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-confirmed-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToCancelled(Appointment appointment, String providerEmail, String timezone, String reason){
        AppointmentCancelledForKafkaDTO dto = appointmentMapper.entityToCancelledForKafkaDTO(appointment, providerEmail, timezone, reason);
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentCancelledForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-cancelled-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCancelledKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToDeleted(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        String traceId = MDC.get(TRACE_ID_KEY);
        Message<AppointmentForKafkaDTO> message = MessageBuilder
                .withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, "appointment-deleted-topic")
                .setHeader(TRACE_ID_KEY, traceId)
                .build();
        try {
            appointmentCreateKafkaTemplate.send(message).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }
    }

}
