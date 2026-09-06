package com.platform.booking.recording.provider_service.repositories;

import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WorkingHoursRepositoryTest extends AbstractBaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    private Provider provider1;
    private Provider provider2;

    @BeforeEach
    void setUp() {
        provider1 = createProvider("John Hairdresser", "provider1@example.com", "HAIRCUT", "UTC");
        entityManager.persist(provider1);

        provider2 = createProvider("Jane Doctor", "provider2@example.com", "MEDICAL", "Europe/Kyiv");
        entityManager.persist(provider2);

        WorkingHours wh1 = new WorkingHours();
        wh1.setProvider(provider1);
        wh1.setDayOfWeek(DayOfWeek.MONDAY.getValue());
        wh1.setStartTime(LocalTime.of(9, 0));
        wh1.setEndTime(LocalTime.of(17, 0));
        entityManager.persist(wh1);

        WorkingHours wh2 = new WorkingHours();
        wh2.setProvider(provider1);
        wh2.setDayOfWeek(DayOfWeek.TUESDAY.getValue());
        wh2.setStartTime(LocalTime.of(9, 0));
        wh2.setEndTime(LocalTime.of(17, 0));
        entityManager.persist(wh2);

        WorkingHours wh3 = new WorkingHours();
        wh3.setProvider(provider2);
        wh3.setDayOfWeek(DayOfWeek.WEDNESDAY.getValue());
        wh3.setStartTime(LocalTime.of(10, 0));
        wh3.setEndTime(LocalTime.of(18, 0));
        entityManager.persist(wh3);

        entityManager.flush();
    }

    private Provider createProvider(String name, String email, String serviceType, String timezone) {
        Provider provider = new Provider();
        provider.setId(UUID.randomUUID());
        provider.setName(name);
        provider.setEmail(email);
        provider.setServiceType(serviceType);
        provider.setTimezone(timezone);
        provider.setAvatarURL("https://example.com/avatar.png");
        provider.setCreatedAt(OffsetDateTime.now());
        provider.setIsBlocked(false);
        return provider;
    }

    @Test
    @DisplayName("findAllByProvider: Returns all working hours for a given Provider entity")
    void findAllByProvider_ReturnsCorrectWorkingHours() {
        // Act
        List<WorkingHours> result = workingHoursRepository.findAllByProvider(provider1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(WorkingHours::getDayOfWeek)
                .containsExactlyInAnyOrder(DayOfWeek.MONDAY.getValue(), DayOfWeek.TUESDAY.getValue());
    }

    @Test
    @DisplayName("findAllByProvider_Id: Returns all working hours for a given Provider UUID")
    void findAllByProvider_Id_ReturnsCorrectWorkingHours() {
        // Act
        List<WorkingHours> result = workingHoursRepository.findAllByProvider_Id(provider1.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(wh -> wh.getProvider().getId().equals(provider1.getId()));
    }

    @Test
    @DisplayName("findAllByProvider_Id: Returns empty list when provider has no working hours or ID is unknown")
    void findAllByProvider_Id_ReturnsEmptyList_WhenNotFound() {
        // Act
        List<WorkingHours> result = workingHoursRepository.findAllByProvider_Id(UUID.randomUUID());

        // Assert
        assertThat(result).isEmpty();
    }
}