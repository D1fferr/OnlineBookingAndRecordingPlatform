package com.platform.booking.recording.AuthService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UserForGetRequestDTO {
    private UUID id;
    private String email;
    private String role;
    private String avatarURL;
    private Boolean isBlocked;
    private String blockReason;

}
