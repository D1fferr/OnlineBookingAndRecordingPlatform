package com.platform.booking.recording.provider_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ServicePageDTO {
    private List<ServiceGetDTO> dtos;
    private UUID providerId;
    private Integer totalPages;
    private Long totalElements;
}
