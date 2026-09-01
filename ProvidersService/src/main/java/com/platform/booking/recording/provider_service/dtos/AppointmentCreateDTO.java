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
    @NotNull(message = "This field cannot be empty")
    private UUID providerId;
    @NotNull(message = "This field cannot be empty")
    @Future(message = "The time must be in the future.")
    private OffsetDateTime startTime;
    @NotNull(message = "This field cannot be empty")
    @Future(message = "The time must be in the future.")
    private OffsetDateTime endTime;
    @NotEmpty(message = "This field cannot be empty")
    private String clientName;
    @NotEmpty(message = "This field cannot be empty")
    private String clientEmail;
    @NotEmpty(message = "This field cannot be empty")
    private String clientComment;
    @NotNull(message = "This field cannot be empty")
    private UUID serviceId;
}
