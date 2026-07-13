package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
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
}
