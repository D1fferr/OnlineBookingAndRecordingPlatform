package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ServiceProviderForGetClientRequestDTO {
    private String serviceName;
    private Integer duration;
    private Double price;

}
