package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Provider;
import com.platform.booking.recording.ProvidersService.models.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {
    List<WorkingHours> findAllByProvider(Provider provider);
    List<WorkingHours> findAllByProvider_Id(UUID providerId);
}
