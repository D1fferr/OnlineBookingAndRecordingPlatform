package com.platform.booking.recording.EmailService.services;

import com.platform.booking.recording.EmailService.dtos.AppointmentCancelledDTO;
import com.platform.booking.recording.EmailService.dtos.AppointmentCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAppointmentConsumerService {
    private final EmailAppointmentSenderService emailAppointmentSenderService;

    @KafkaListener(topics = "appointment-create-topic",
            containerFactory = "providerAppointmentKafkaListenerContainerFactory")
    public void getAppointmentCreate(AppointmentCreateDTO dto){
        emailAppointmentSenderService.sendCreateMessageToClient(dto);
        emailAppointmentSenderService.sendCreateMessageToProvider(dto);
    }
    @KafkaListener(topics = "appointment-confirmed-topic",
            containerFactory = "providerAppointmentKafkaListenerContainerFactory")
    public void getAppointmentConfirmed(AppointmentCreateDTO dto){
        emailAppointmentSenderService.sendConfirmedMessage(dto);
    }
    @KafkaListener(topics = "appointment-cancelled-topic",
            containerFactory = "providerAppointmentCancelledKafkaListenerContainerFactory")
    public void getAppointmentCancelled(AppointmentCancelledDTO dto){
        emailAppointmentSenderService.sendCancelledMessage(dto);
    }
    @KafkaListener(topics = "appointment-deleted-topic",
            containerFactory = "providerAppointmentKafkaListenerContainerFactory")
    public void getAppointmentDeleted(AppointmentCreateDTO dto){
        emailAppointmentSenderService.sendDeletedMessageToProvider(dto);
    }

}
