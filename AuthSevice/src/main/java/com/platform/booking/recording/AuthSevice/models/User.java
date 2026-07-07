package com.platform.booking.recording.AuthSevice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;
    @Column(name = "email")
    @Email
    private String email;
    @Column(name = "password_hash")
    @NotEmpty
    private String password;
    @Column(name = "role")
    private String role;
    @Column(name = "avatar_url")
    private String avatarURL;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
