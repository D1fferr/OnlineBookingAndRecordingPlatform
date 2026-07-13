package com.platform.booking.recording.ProvidersService.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class WorkingHoursCreateDTO {

    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    @NotEmpty
    private Boolean isActive;

}
