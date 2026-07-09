package com.platform.booking.recording.AuthService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginDTO {
    private String email;
    private String password;
}
