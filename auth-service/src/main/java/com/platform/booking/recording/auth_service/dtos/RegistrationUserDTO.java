package com.platform.booking.recording.auth_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationUserDTO {
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotEmpty(message = "This field cannot be empty")
    private String name;
    @NotEmpty(message = "This field cannot be empty")
    private String serviceType;
    @NotEmpty(message = "This field cannot be empty")
    private String timezone;

}
