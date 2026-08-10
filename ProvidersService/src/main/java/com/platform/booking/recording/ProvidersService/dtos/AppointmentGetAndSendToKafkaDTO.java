package com.platform.booking.recording.ProvidersService.dtos;

import com.platform.booking.recording.ProvidersService.models.Appointment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentGetAndSendToKafkaDTO {
    private AppointmentGetForCreateDTO dto;
    private Appointment appointment;
}
