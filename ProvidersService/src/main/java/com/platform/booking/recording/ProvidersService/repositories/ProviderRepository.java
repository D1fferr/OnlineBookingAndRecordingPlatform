package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    @EntityGraph(attributePaths = {"serviceProviders"})
    @Query("""
    SELECT p FROM Provider p 
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
    GROUP BY p.id
    ORDER BY COUNT(a) DESC, p.createdAt DESC
""")
    Page<Provider> findProviders(
            @Param("searchTerm") String searchTerm,
            @Param("category") String category,
            Pageable pageable
    );

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
    Optional<Provider> findByIdAndIsBlocked(UUID id, Boolean isBlocked);
}
