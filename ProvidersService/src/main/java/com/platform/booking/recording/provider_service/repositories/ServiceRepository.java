package com.platform.booking.recording.provider_service.repositories;

import com.platform.booking.recording.provider_service.models.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceProvider, UUID> {
    Page<ServiceProvider> findAllByProvider_Id(UUID providerId, Pageable pageable);
    @EntityGraph(attributePaths = {"provider", "provider.workingHours"})
    Optional<ServiceProvider> findWithProviderAndWorkingHoursById(UUID id);
}
