package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProviderChangeDataDTO {
    private String name;
    private String serviceType;
    private String timezone;
}
