package com.platform.booking.recording.AuthService.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ExceptionErrorResponse {
    private String message;
}
