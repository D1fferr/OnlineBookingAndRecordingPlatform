package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCancelledForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentForKafkaDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentGetDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.Provider;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentForKafkaDTO entityToForKafkaDTO(Appointment appointment, String providerEmail, String timezone){
        AppointmentForKafkaDTO dto = new AppointmentForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(providerEmail);
        dto.setTimezone(timezone);
        return dto;
    }
    public AppointmentCancelledForKafkaDTO entityToCancelledForKafkaDTO(Appointment appointment, String providerEmail, String timezone, String reason){
        AppointmentCancelledForKafkaDTO dto = new AppointmentCancelledForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(providerEmail);
        dto.setTimezone(timezone);
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



}
