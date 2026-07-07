package com.platform.booking.recording.AuthSevice.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationUserDTO {
    private String email;
    private String password;
    private String name;
    private String serviceType;
    private String timezone;

}
