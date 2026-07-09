package com.platform.booking.recording.AuthService.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeCredentialsDTO {

    @Email
    private String email;
    private String password;
    private String currentPassword;
}
