package com.platform.booking.recording.AuthService.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UserForKafkaDTO {
    private UUID id;
    private String name;
    private String email;
    private String serviceType;
    private String timezone;
    private String avatarURL;

}
