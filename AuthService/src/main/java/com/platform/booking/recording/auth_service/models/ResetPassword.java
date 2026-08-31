package com.platform.booking.recording.auth_service.models;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RedisHash(value = "reset_code")
public class ResetPassword {
    @Id
    private String email;
    private String code;
    @TimeToLive
    private Long ttlInSeconds;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResetPassword that = (ResetPassword) o;
        return Objects.equals(email, that.email) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, code);
    }
}
