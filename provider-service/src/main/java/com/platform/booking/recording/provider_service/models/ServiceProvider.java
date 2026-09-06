package com.platform.booking.recording.provider_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "services")
public class ServiceProvider {

    @Id
    @Column(name = "id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    @Column(name = "service_name")
    @NotEmpty(message = "This field cannot be empty")
    private String serviceName;
    @Column(name = "duration")
    @NotNull(message = "This field cannot be empty")
    private Integer duration;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private Provider provider;
    @Column(name = "price")
    @NotNull(message = "This field cannot be empty")
    private Double price;
    @Column(name = "description")
    @NotEmpty(message = "This field cannot be empty")
    private String description;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
