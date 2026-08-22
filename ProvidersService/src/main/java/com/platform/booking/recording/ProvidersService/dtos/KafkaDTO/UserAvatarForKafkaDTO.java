package com.platform.booking.recording.ProvidersService.dtos.KafkaDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserAvatarForKafkaDTO {
    private UUID id;
    private String avatarURL;
}
