package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentGetForCreateDTO {
    private String service;
    private String providerName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double price;
    private String clientName;
    private String clientEmail;
    private String clientComment;

}
