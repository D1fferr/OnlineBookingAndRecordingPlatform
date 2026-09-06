package com.platform.booking.recording.auth_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
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
