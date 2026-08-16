package com.platform.booking.recording.ProvidersService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
@Setter
public class FreeSlotDTO {
    private LocalTime startTime;
    private LocalTime endTime;
}
