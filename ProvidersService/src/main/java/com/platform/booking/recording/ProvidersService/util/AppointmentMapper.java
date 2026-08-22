package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.*;
import com.platform.booking.recording.ProvidersService.dtos.KafkaDTO.*;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentForKafkaDTO entityToCreateForKafkaDTO(Appointment appointment){
        AppointmentForKafkaDTO dto = new AppointmentCreateForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(appointment.getProvider().getEmail());
        dto.setTimezone(appointment.getProvider().getTimezone());
        return dto;
    }
    public AppointmentForKafkaDTO entityConfirmedToForKafkaDTO(Appointment appointment){
        AppointmentForKafkaDTO dto = new AppointmentConfirmedForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(appointment.getProvider().getEmail());
        dto.setTimezone(appointment.getProvider().getTimezone());
        return dto;
    }
    public AppointmentForKafkaDTO entityToDeletedForKafkaDTO(Appointment appointment){
        AppointmentForKafkaDTO dto = new AppointmentDeletedForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(appointment.getProvider().getEmail());
        dto.setTimezone(appointment.getProvider().getTimezone());
        return dto;
    }
    public AppointmentCancelledForKafkaDTO entityToCancelledForKafkaDTO(Appointment appointment, String reason){
        AppointmentCancelledForKafkaDTO dto = new AppointmentCancelledForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(appointment.getProvider().getEmail());
        dto.setTimezone(appointment.getProvider().getTimezone());
        dto.setReason(reason);
        return dto;
    }

    public Appointment createDTOtoEntity(AppointmentCreateDTO dto, Provider provider){
        Appointment appointment = new Appointment();
        appointment.setClientComment(dto.getClientComment());
        appointment.setClientEmail(dto.getClientEmail());
        appointment.setClientName(dto.getClientName());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setProvider(provider);
        return appointment;
    }
    public AppointmentGetDTO entityToGetDTO(Appointment appointment){
        AppointmentGetDTO dto = new AppointmentGetDTO();
        dto.setId(appointment.getId());
        dto.setProviderId(appointment.getProvider().getId());
        dto.setStatus(appointment.getStatus());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        return dto;
    }
    public AppointmentGetForCreateDTO entityToGetForCreateDTO(Appointment appointment, ServiceProvider serviceProvider){
        AppointmentGetForCreateDTO dto = new AppointmentGetForCreateDTO();
        dto.setProviderName(appointment.getProvider().getName());
        dto.setService(serviceProvider.getServiceName());
        dto.setPrice(serviceProvider.getPrice());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        return dto;
    }



}
