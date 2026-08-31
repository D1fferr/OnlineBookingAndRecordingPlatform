package com.platform.booking.recording.provider_service.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentCreateDTO {
    @NotNull
    private UUID providerId;
    @NotNull
    @Future
    private OffsetDateTime startTime;
    @NotNull
    @Future
    private OffsetDateTime endTime;
    @NotEmpty
    private String clientName;
    @NotEmpty
    private String clientEmail;
    @NotEmpty
    private String clientComment;
    @NotNull
    private UUID serviceId;
}
