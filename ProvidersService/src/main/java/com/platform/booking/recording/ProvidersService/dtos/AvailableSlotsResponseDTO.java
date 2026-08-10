package com.platform.booking.recording.ProvidersService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AvailableSlotsResponseDTO {
    private String timezone;
    private List<DaySlotsDTO> appointments;
}