package com.platform.booking.recording.provider_service.repositories;

import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {
    List<WorkingHours> findAllByProvider(Provider provider);
    List<WorkingHours> findAllByProvider_Id(UUID providerId);
}
