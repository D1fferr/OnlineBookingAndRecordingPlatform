package com.platform.booking.recording.provider_service.repositories;

import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceRepositoryTest extends AbstractBaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    private Provider provider;
    private ServiceProvider service1;
    private ServiceProvider service2;

    @BeforeEach
    void setUp() {
        provider = new Provider();
        provider.setId(UUID.randomUUID());
        provider.setName("John Smith Barber");
        provider.setEmail("barber@example.com");
        provider.setServiceType("HAIRCUT");
        provider.setTimezone("UTC");
        provider.setAvatarURL("https://example.com/avatar.png");
        provider.setCreatedAt(OffsetDateTime.now());
        provider.setIsBlocked(false);
        entityManager.persist(provider);

        WorkingHours wh = new WorkingHours();
        wh.setProvider(provider);
        wh.setDayOfWeek(DayOfWeek.MONDAY.getValue());
        wh.setStartTime(LocalTime.of(9, 0));
        wh.setEndTime(LocalTime.of(17, 0));
        entityManager.persist(wh);

        service1 = createServiceProvider("Men Haircut", 25.0, 30, provider);
        service2 = createServiceProvider("Beard Trim", 15.0, 20, provider);

        entityManager.persist(service1);
        entityManager.persist(service2);

        entityManager.flush();
        entityManager.clear();
    }

    private ServiceProvider createServiceProvider(String serviceName, Double price, Integer duration, Provider provider) {
        ServiceProvider service = new ServiceProvider();
        service.setServiceName(serviceName);
        service.setDescription("Professional service for testing");
        service.setPrice(price);
        service.setDuration(duration);
        service.setProvider(provider);
        service.setCreatedAt(OffsetDateTime.now());
        service.setUpdatedAt(OffsetDateTime.now());
        return service;
    }

    @Test
    @DisplayName("findAllByProvider_Id: Returns paginated ServiceProvider entities for a given Provider UUID")
    void findAllByProvider_Id_ReturnsPaginatedResult() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<ServiceProvider> result = serviceRepository.findAllByProvider_Id(provider.getId(), pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findWithProviderAndWorkingHoursById: Fetches ServiceProvider along with initialized Provider and WorkingHours")
    void findWithProviderAndWorkingHoursById_EagerlyLoadsEntityGraph() {
        // Act
        Optional<ServiceProvider> result = serviceRepository.findWithProviderAndWorkingHoursById(service1.getId());

        // Assert
        assertThat(result).isPresent();
        ServiceProvider fetchedService = result.get();
        assertThat(fetchedService.getServiceName()).isEqualTo("Men Haircut");

        // Verify that relationships are initialized without throwing LazyInitializationException
        assertThat(Hibernate.isInitialized(fetchedService.getProvider())).isTrue();
        assertThat(fetchedService.getProvider().getWorkingHours()).isNotEmpty();
        assertThat(Hibernate.isInitialized(fetchedService.getProvider().getWorkingHours())).isTrue();
    }

    @Test
    @DisplayName("findWithProviderAndWorkingHoursById: Returns empty Optional when entity ID does not exist")
    void findWithProviderAndWorkingHoursById_ReturnsEmpty_WhenNotFound() {
        // Act
        Optional<ServiceProvider> result = serviceRepository.findWithProviderAndWorkingHoursById(UUID.randomUUID());

        // Assert
        assertThat(result).isEmpty();
    }
}
