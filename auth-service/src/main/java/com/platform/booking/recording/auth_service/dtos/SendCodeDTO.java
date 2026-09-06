package com.platform.booking.recording.auth_service.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendCodeDTO {
    @Email(message = "Please provide a valid email address")
    private String email;
}
