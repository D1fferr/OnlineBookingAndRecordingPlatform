package com.platform.booking.recording.ProvidersService.util;

import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateDTO;
import com.platform.booking.recording.ProvidersService.dtos.AppointmentCreateForKafkaDTO;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.Provider;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentCreateForKafkaDTO entityToCreateForKafkaDTO(Appointment appointment, String providerEmail){
        AppointmentCreateForKafkaDTO dto = new AppointmentCreateForKafkaDTO();
        dto.setSecureToken(appointment.getSecureToken());
        dto.setEndTime(appointment.getEndTime());
        dto.setStartTime(appointment.getStartTime());
        dto.setClientName(appointment.getClientName());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientComment(appointment.getClientComment());
        dto.setProviderEmail(providerEmail);
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


}
