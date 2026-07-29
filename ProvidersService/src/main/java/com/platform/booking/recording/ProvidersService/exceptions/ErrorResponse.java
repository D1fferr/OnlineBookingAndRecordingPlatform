package com.platform.booking.recording.ProvidersService.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    private Integer status;
    private String error;
    private List<ValidationError> validationErrors;
}
