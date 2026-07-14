package com.platform.booking.recording.ProvidersService.services;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.util.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAppointmentProducerService {

    private final KafkaTemplate<String, AppointmentCreateForKafkaDTO> appointmentCreateKafkaTemplate;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentService appointmentService;

    public void sendToCreate(Appointment appointment, String providerEmail){
        AppointmentCreateForKafkaDTO dto = appointmentMapper.entityToCreateForKafkaDTO(appointment, providerEmail);
        try {
            appointmentCreateKafkaTemplate.send("appointment-create-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToConfirmed(Appointment appointment, String providerEmail){
        AppointmentCreateForKafkaDTO dto = appointmentMapper.entityToCreateForKafkaDTO(appointment, providerEmail);
        try {
            appointmentCreateKafkaTemplate.send("appointment-confirmed-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToCancelled(Appointment appointment, String providerEmail){
        AppointmentCreateForKafkaDTO dto = appointmentMapper.entityToCreateForKafkaDTO(appointment, providerEmail);
        try {
            appointmentCreateKafkaTemplate.send("appointment-cancelled-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }
    public void sendToDeleted(Appointment appointment, String providerEmail){
        AppointmentCreateForKafkaDTO dto = appointmentMapper.entityToCreateForKafkaDTO(appointment, providerEmail);
        try {
            appointmentCreateKafkaTemplate.send("appointment-deleted-topic", dto).get();
            appointmentService.setIsRemindedSentToTrue(appointment.getId());
        }catch (Exception e){
            //add logs with cause
            throw new KafkaException(e.getMessage());
        }
    }

}
