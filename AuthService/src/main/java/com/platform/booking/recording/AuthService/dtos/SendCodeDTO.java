package com.platform.booking.recording.AuthService.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendCodeDTO {
    @Email
    private String email;
}
