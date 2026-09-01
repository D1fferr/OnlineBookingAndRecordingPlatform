package com.platform.booking.recording.auth_service.dtos;

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
    @NotEmpty(message = "This field cannot be empty")
    private String reason;
}
