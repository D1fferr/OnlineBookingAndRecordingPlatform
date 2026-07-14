package com.platform.booking.recording.ProvidersService.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @Column(name = "id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;
    @Column(name = "start_time")
    private OffsetDateTime startTime;
    @Column(name = "end_time")
    private OffsetDateTime endTime;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "client_email")
    private String clientEmail;
    @Column(name = "client_comment")
    private String clientComment;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointmentsStatus status;
    @Column(name = "is_reminder_sent")
    private Boolean isReminderSent;
    @Column(name = "secure_token")
    private UUID secureToken;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
