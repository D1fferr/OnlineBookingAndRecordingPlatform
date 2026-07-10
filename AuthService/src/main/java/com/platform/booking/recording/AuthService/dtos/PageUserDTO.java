package com.platform.booking.recording.AuthService.dtos;

import jdk.dynalink.linker.LinkerServices;
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
