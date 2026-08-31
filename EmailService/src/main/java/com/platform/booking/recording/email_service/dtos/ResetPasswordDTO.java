package com.platform.booking.recording.email_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {
    private String email;
    private String code;
    private Long ttlInSeconds;

}
