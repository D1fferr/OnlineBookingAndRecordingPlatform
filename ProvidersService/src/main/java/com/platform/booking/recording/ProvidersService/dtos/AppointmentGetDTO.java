package com.platform.booking.recording.ProvidersService.dtos;

import com.platform.booking.recording.ProvidersService.models.AppointmentsStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentGetDTO {
    private UUID id;
    private UUID providerId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String clientName;
    private String clientEmail;
    private String clientComment;
    private AppointmentsStatus status;


}
