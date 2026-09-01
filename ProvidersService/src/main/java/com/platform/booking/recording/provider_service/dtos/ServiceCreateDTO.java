package com.platform.booking.recording.provider_service.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ServiceCreateDTO {
    @NotNull(message = "This field cannot be empty")
    private UUID providerId;
    @NotEmpty(message = "This field cannot be empty")
    private String serviceName;
    @NotNull(message = "This field cannot be empty")
    private Integer duration;
    @NotNull(message = "This field cannot be empty")
    private Double price;
    @NotEmpty(message = "This field cannot be empty")
    private String description;
}
