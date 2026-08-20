package com.platform.booking.recording.ProvidersService.dtos;

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
    @NotNull
    private UUID providerId;
    @NotEmpty
    private String serviceName;
    @NotNull
    private Integer duration;
    @NotNull
    private Double price;
    @NotEmpty
    private String description;
}
