package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledReasonDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAppointmentProducerService {

    private final KafkaTemplate<String, AppointmentForKafkaDTO> appointmentCreateKafkaTemplate;
    private final KafkaTemplate<String, AppointmentCancelledForKafkaDTO> appointmentCancelledKafkaTemplate;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentService appointmentService;

    public void sendToCreate(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        try {
            appointmentCreateKafkaTemplate.send("appointment-create-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToConfirmed(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        try {
            appointmentCreateKafkaTemplate.send("appointment-confirmed-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToCancelled(Appointment appointment, String providerEmail, String timezone, String reason){
        AppointmentCancelledForKafkaDTO dto = appointmentMapper.entityToCancelledForKafkaDTO(appointment, providerEmail, timezone, reason);
        try {
            appointmentCancelledKafkaTemplate.send("appointment-cancelled-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToDeleted(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = appointmentMapper.entityToForKafkaDTO(appointment, providerEmail, timezone);
        try {
            appointmentCreateKafkaTemplate.send("appointment-deleted-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }

}
