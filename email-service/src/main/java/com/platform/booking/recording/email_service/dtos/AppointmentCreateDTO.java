package com.platform.booking.recording.email_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AppointmentCreateDTO {
    private UUID secureToken;
    private String clientName;
    private String clientEmail;
    private String clientComment;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String providerEmail;
    private String timezone;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentCreateDTO that = (AppointmentCreateDTO) o;
        return Objects.equals(secureToken, that.secureToken) && Objects.equals(clientName, that.clientName) && Objects.equals(clientEmail, that.clientEmail) && Objects.equals(clientComment, that.clientComment) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(providerEmail, that.providerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secureToken, clientName, clientEmail, clientComment, startTime, endTime, providerEmail);
    }
}
