package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProviderPageForGetClientRequestDTO {
    private List<ProviderForGetClientRequestDTO> dtos;
    private Integer totalPages;
    private Long totalElements;
}
