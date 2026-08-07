package com.platform.booking.recording.ProvidersService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.serialization.IntegerDeserializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProviderPageForGetClientRequestDTO {
    private List<ProviderForGetClientRequestDTO> dtos;
    private Integer totalPages;
    private Long totalElements;
}
