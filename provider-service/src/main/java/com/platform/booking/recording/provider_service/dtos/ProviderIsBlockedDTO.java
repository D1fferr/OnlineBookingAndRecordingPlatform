package com.platform.booking.recording.provider_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProviderIsBlockedDTO {
    private UUID id;
    private Boolean isBlocked;
}
