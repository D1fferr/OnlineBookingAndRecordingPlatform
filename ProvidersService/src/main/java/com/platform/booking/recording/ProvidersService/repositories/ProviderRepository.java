package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.dtos.ProviderAndServiceProjection;
import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.Provider;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    @Query(
            value = """
            SELECT p.id FROM Provider p
            LEFT JOIN p.serviceProviders s
            LEFT JOIN p.appointments a
            WHERE p.isBlocked = false
            AND (:category IS NULL OR :category = '' OR LOWER(p.serviceType) = LOWER(:category))
            AND (
                :searchTerm IS NULL OR :searchTerm = '' OR
                LOWER(p.name) LIKE LOWER(:searchTerm) OR
                LOWER(p.serviceType) LIKE LOWER(:searchTerm) OR
                LOWER(s.serviceName) LIKE LOWER(:searchTerm) OR
                LOWER(s.description) LIKE LOWER(:searchTerm)
            )
            GROUP BY p.id, p.createdAt
            ORDER BY COUNT(a) DESC, p.createdAt DESC
        """,
            countQuery = """
            SELECT COUNT(DISTINCT p.id) FROM Provider p
            LEFT JOIN p.serviceProviders s
            WHERE p.isBlocked = false
            AND (:category IS NULL OR :category = '' OR LOWER(p.serviceType) = LOWER(:category))
            AND (
                :searchTerm IS NULL OR :searchTerm = '' OR
                LOWER(p.name) LIKE LOWER(:searchTerm) OR
                LOWER(p.serviceType) LIKE LOWER(:searchTerm) OR
                LOWER(s.serviceName) LIKE LOWER(:searchTerm) OR
                LOWER(s.description) LIKE LOWER(:searchTerm)
            )
        """
    )
    Page<UUID> findProviderIds(
            @Param("searchTerm") String searchTerm,
            @Param("category") String category,
            Pageable pageable
    );
    @EntityGraph(attributePaths = {"serviceProviders"})
    @Query("SELECT DISTINCT p FROM Provider p WHERE p.id IN :ids")
    List<Provider> findAllByIdsIn(@Param("ids") List<UUID> ids);

    @Query("""
    SELECT DISTINCT p.serviceType
    FROM Provider p
    WHERE p.isBlocked = false
    AND p.serviceType IS NOT NULL
    AND p.serviceType != ''
    ORDER BY p.serviceType ASC
""")
    List<String> findAllUniqueServiceTypes();

    @EntityGraph(attributePaths = {"serviceProviders", "workingHours"})
    @Query("SELECT DISTINCT p FROM Provider p WHERE p.id = :id AND p.isBlocked = :isBlocked")
    Optional<Provider> findByIdAndIsBlocked(@Param("id") UUID id, @Param("isBlocked") Boolean isBlocked);
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.provider.id = :providerId
        AND a.createdAt >= :startDate
        AND a.createdAt <= :endDate
        AND a.status != 'CANCELLED'
    """)
    List<Appointment> findBookedAppointmentsForPeriod(
            @Param("providerId") UUID providerId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT new com.platform.booking.recording.ProvidersService.dtos.ProviderAndServiceProjection(p, s)
    FROM Provider p
    LEFT JOIN p.serviceProviders s ON s.id = :serviceId
    WHERE p.id = :providerId AND p.isBlocked = false AND s.id = :serviceId
""")
    Optional<ProviderAndServiceProjection> findByIdWithLock(
            @Param("providerId") UUID providerId,
            @Param("serviceId") UUID serviceId
    );
}
