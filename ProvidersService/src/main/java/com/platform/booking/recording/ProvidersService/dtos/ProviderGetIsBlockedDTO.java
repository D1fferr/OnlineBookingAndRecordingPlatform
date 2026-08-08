package com.platform.booking.recording.ProvidersService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ProviderGetIsBlockedDTO {
    private UUID id;
    private Boolean isBlocked;
}
