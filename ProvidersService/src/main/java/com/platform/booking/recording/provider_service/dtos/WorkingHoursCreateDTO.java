package com.platform.booking.recording.provider_service.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
public class WorkingHoursCreateDTO {

    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private Integer slotStep;
    @NotEmpty
    private Boolean isActive;

}
