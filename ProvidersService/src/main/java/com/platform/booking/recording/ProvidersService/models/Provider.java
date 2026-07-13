package com.platform.booking.recording.ProvidersService.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "providers")
public class Provider {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    @Email
    private String email;
    @Column(name = "service_type")
    private String serviceType;
    @Column(name = "timezone")
    private String timezone;
    @Column(name = "avatar_url")
    private String avatarURL;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkingHours> workingHours = new ArrayList<>();
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();
}
