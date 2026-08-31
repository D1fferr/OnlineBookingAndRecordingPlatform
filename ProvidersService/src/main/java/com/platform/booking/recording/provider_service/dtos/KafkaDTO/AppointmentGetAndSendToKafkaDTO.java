package com.platform.booking.recording.provider_service.dtos.KafkaDTO;

import com.platform.booking.recording.provider_service.dtos.AppointmentGetForCreateDTO;
import com.platform.booking.recording.provider_service.models.Appointment;
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
