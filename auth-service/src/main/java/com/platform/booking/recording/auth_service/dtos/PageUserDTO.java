package com.platform.booking.recording.auth_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class PageUserDTO {
    private List<UserForGetRequestDTO> dtos;
    private Integer totalPages;
    private Long totalElements;
}
