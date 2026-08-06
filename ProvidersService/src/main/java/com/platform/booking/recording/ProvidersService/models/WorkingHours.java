package com.platform.booking.recording.ProvidersService.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "working_hours")
public class WorkingHours {
    @Id
    @Column(name = "id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;
    @Column(name = "day_of_week")
    private Integer dayOfWeek;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "break_start_time")
    private LocalTime breakStartTime;
    @Column(name = "break_end_time")
    private LocalTime breakEndTime;
    @Column(name = "slot_step")
    private Integer slotStep;
    @Column(name = "is_active")
    private Boolean isActive;
}
