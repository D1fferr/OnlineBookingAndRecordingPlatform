package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DaySlotsDTO {
    private Integer dayOfWeek;
    private LocalDate date;
    private List<FreeSlotDTO> freeSlots;
}
