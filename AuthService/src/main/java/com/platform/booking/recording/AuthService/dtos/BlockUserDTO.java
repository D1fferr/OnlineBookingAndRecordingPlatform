package com.platform.booking.recording.AuthService.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class BlockUserDTO {
    @NotNull
    private UUID userId;
    @NotEmpty
    private String reason;
}
