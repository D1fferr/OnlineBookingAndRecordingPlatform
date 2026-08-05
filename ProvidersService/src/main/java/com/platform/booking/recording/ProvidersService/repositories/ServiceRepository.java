package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceProvider, UUID> {
    Page<ServiceProvider> findAllByProvider_Id(UUID providerId, Pageable pageable);
}
