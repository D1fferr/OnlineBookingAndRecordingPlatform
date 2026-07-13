package com.platform.booking.recording.ProvidersService.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ListWorkingHoursCreateDTO {

    private List<WorkingHoursCreateDTO> workingHoursCreateDTOList;
    @NotNull
    private UUID providerId;
}
