package com.platform.booking.recording.ProvidersService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentForKafkaDTO {
    private UUID secureToken;
    private String clientName;
    private String clientEmail;
    private String clientComment;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String providerEmail;
    private String timezone;

}
