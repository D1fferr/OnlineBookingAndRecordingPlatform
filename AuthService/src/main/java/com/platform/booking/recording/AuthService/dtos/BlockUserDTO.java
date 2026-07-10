package com.platform.booking.recording.AuthService.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class BlockUserDTO {
    @NotEmpty
    private UUID userId;
    @NotEmpty
    private String reason;
}
