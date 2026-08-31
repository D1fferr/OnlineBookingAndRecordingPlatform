package com.platform.booking.recording.provider_service.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentCancelledReasonDTO {
    @NotEmpty
    private String reason;
}
