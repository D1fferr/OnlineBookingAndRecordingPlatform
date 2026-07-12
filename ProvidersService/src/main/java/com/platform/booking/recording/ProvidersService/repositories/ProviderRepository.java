package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {



}
