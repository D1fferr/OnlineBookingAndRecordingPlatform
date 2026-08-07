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

import java.util.UUID;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {

    @EntityGraph(attributePaths = {"serviceProviders"})
    @Query("""
    SELECT DISTINCT p FROM Provider p 
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
""")
    Page<Provider> findProviders(
            @Param("searchTerm") String searchTerm,
            @Param("category") String category,
            Pageable pageable
    );

    Page<Provider> findProvidersByServiceType(String serviceType, Pageable pageable);
}
