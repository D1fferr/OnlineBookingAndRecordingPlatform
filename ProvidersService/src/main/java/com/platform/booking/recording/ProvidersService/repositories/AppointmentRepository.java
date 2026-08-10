package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Appointment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.provider.id = :providerId
        AND a.startTime < :requestedEndTime
        AND a.endTime > :requestedStartTime
    """)
    boolean existsOverlappingAppointment(
            @Param("providerId") UUID providerId,
            @Param("requestedStartTime") OffsetDateTime requestedStartTime,
            @Param("requestedEndTime") OffsetDateTime requestedEndTime
    );

    @Query("SELECT a FROM Appointment a JOIN FETCH a.provider WHERE a.id = :id")
    Optional<Appointment> findByIdWithProvider(@Param("id") UUID id);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.provider WHERE a.secureToken = :token")
    Optional<Appointment> findBySecureTokenWithProvider(@Param("token") UUID token);

    Page<Appointment> findAllByProvider_Id(UUID providerId, Pageable pageable);

    @Query("""
    SELECT a FROM Appointment a
    WHERE (
        LOWER(a.clientName) LIKE LOWER(:search) OR
        LOWER(a.clientEmail) LIKE LOWER(:search) OR
        LOWER(a.clientComment) LIKE LOWER(:search)
    )
    AND a.provider.id = :id
""")
    Page<Appointment> findAppointments(
            @Param("search") String search,
            @Param("id") UUID id,
            Pageable pageable
    );
}
