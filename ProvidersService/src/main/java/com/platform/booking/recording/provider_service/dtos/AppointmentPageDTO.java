package com.platform.booking.recording.provider_service.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentPageDTO {
    private List<AppointmentGetDTO> dtoList;
    private Integer totalPages;
    private Long totalElements;
}
