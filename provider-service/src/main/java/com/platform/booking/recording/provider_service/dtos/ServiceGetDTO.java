package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ServiceGetDTO {
    private UUID id;
    private String serviceName;
    private Integer duration;
    private Double price;
    private String description;
}
