package com.platform.booking.recording.provider_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "This field cannot be empty")
    private Provider provider;
    @Column(name = "start_time")
    @NotEmpty(message = "This field cannot be empty")
    @Future(message = "The time must be in the future.")
    private OffsetDateTime startTime;
    @Column(name = "end_time")
    @NotEmpty(message = "This field cannot be empty")
    @Future(message = "The time must be in the future.")
    private OffsetDateTime endTime;
    @Column(name = "client_name")
    @NotEmpty(message = "This field cannot be empty")
    private String clientName;
    @Column(name = "client_email")
    @NotEmpty(message = "This field cannot be empty")
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
