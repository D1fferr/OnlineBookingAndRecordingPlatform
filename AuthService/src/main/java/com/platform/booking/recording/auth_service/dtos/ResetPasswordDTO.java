package com.platform.booking.recording.auth_service.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {
    private String code;
    @Email
    private String email;
    private String newPassword;

}
