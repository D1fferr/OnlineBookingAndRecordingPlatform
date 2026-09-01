package com.platform.booking.recording.provider_service.repositories;


import com.platform.booking.recording.provider_service.dtos.ProviderAndServiceProjection;
import com.platform.booking.recording.provider_service.models.Appointment;
import com.platform.booking.recording.provider_service.models.AppointmentsStatus;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderRepositoryTest extends AbstractBaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProviderRepository providerRepository;

    private Provider provider1;
    private Provider provider2;
    private Provider blockedProvider;
    private ServiceProvider service1;

    @BeforeEach
    void setUp() {
        provider1 = createProvider("Barber Shop Pro", "barber@test.com", "HAIRCUT", "UTC", false);
        entityManager.persist(provider1);

        provider2 = createProvider("Medical Clinic", "clinic@test.com", "MEDICAL", "UTC", false);
        entityManager.persist(provider2);

        blockedProvider = createProvider("Blocked Salon", "blocked@test.com", "HAIRCUT", "UTC", true);
        entityManager.persist(blockedProvider);

        service1 = createServiceProvider("Men Haircut", "Best haircut in town", 25.0, 30, provider1);
        entityManager.persist(service1);

        Appointment appointment1 = createAppointment(provider1, AppointmentsStatus.CONFIRMED, OffsetDateTime.now().minusDays(2));
        Appointment appointment2 = createAppointment(provider1, AppointmentsStatus.CANCELLED, OffsetDateTime.now().minusDays(1));
        entityManager.persist(appointment1);
        entityManager.persist(appointment2);

        entityManager.flush();
        entityManager.clear();
    }

    private Provider createProvider(String name, String email, String serviceType, String timezone, Boolean isBlocked) {
        Provider provider = new Provider();
        provider.setId(UUID.randomUUID());
        provider.setName(name);
        provider.setEmail(email);
        provider.setServiceType(serviceType);
        provider.setTimezone(timezone);
        provider.setAvatarURL("https://example.com/avatar.png");
        provider.setCreatedAt(OffsetDateTime.now());
        provider.setIsBlocked(isBlocked);
        return provider;
    }

    private ServiceProvider createServiceProvider(String serviceName, String description, Double price, Integer duration, Provider provider) {
        ServiceProvider service = new ServiceProvider();
        service.setServiceName(serviceName);
        service.setDescription(description);
        service.setPrice(price);
        service.setDuration(duration);
        service.setProvider(provider);
        service.setCreatedAt(OffsetDateTime.now());
        service.setUpdatedAt(OffsetDateTime.now());
        return service;
    }

    private Appointment createAppointment(Provider provider, AppointmentsStatus status, OffsetDateTime createdAt) {
        Appointment appointment = new Appointment();
        appointment.setProvider(provider);
        appointment.setStatus(status);
        appointment.setClientName("John Doe");
        appointment.setClientEmail("john.doe@example.com");
        appointment.setClientComment("Initial consultation");
        appointment.setStartTime(createdAt);
        appointment.setEndTime(createdAt.plusHours(1));
        appointment.setIsReminderSent(false);
        appointment.setSecureToken(UUID.randomUUID());
        appointment.setCreatedAt(createdAt);
        return appointment;
    }

    @Test
    @DisplayName("findProviderIds: Filters non-blocked providers by search term and category")
    void findProviderIds_FiltersCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert 1: Filter by category HAIRCUT (should return provider1, excluding blockedProvider)
        Page<UUID> haircutPage = providerRepository.findProviderIds(null, "HAIRCUT", pageable);
        assertThat(haircutPage.getContent()).containsExactly(provider1.getId());

        // Act & Assert 2: Filter by search term matching service description
        Page<UUID> searchPage = providerRepository.findProviderIds("%best haircut%", null, pageable);
        assertThat(searchPage.getContent()).containsExactly(provider1.getId());
    }

    @Test
    @DisplayName("findAllByIdsIn: Fetches providers by list of IDs with eager serviceProviders graph")
    void findAllByIdsIn_ReturnsProvidersWithEntityGraph() {
        // Act
        List<Provider> results = providerRepository.findAllByIdsIn(List.of(provider1.getId(), provider2.getId()));

        // Assert
        assertThat(results).hasSize(2);
        Provider fetchedProvider1 = results.stream()
                .filter(p -> p.getId().equals(provider1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(Hibernate.isInitialized(fetchedProvider1.getServiceProviders())).isTrue();
    }

    @Test
    @DisplayName("findAllUniqueServiceTypes: Returns distinct sorted non-null service types of active providers")
    void findAllUniqueServiceTypes_ReturnsUniqueSortedTypes() {
        // Act
        List<String> types = providerRepository.findAllUniqueServiceTypes();

        // Assert
        assertThat(types)
                .contains("HAIRCUT", "MEDICAL")
                .isSortedAccordingTo(String::compareTo);
    }

    @Test
    @DisplayName("findByIdAndIsBlocked: Retrieves provider when blocked status matches")
    void findByIdAndIsBlocked_ReturnsCorrectProvider() {
        // Act
        Optional<Provider> activeResult = providerRepository.findByIdAndIsBlocked(provider1.getId(), false);
        Optional<Provider> blockedResult = providerRepository.findByIdAndIsBlocked(provider1.getId(), true);

        // Assert
        assertThat(activeResult).isPresent();
        assertThat(Hibernate.isInitialized(activeResult.get().getServiceProviders())).isTrue();
        assertThat(blockedResult).isEmpty();
    }

    @Test
    @DisplayName("findBookedAppointmentsForPeriod: Excludes CANCELLED status and respects date boundaries")
    void findBookedAppointmentsForPeriod_ExcludesCancelled() {
        // Arrange
        OffsetDateTime startDate = OffsetDateTime.now().minusDays(5);
        OffsetDateTime endDate = OffsetDateTime.now();

        // Act
        List<Appointment> appointments = providerRepository.findBookedAppointmentsForPeriod(provider1.getId(), startDate, endDate);

        // Assert
        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).getStatus()).isEqualTo(AppointmentsStatus.CONFIRMED);
    }

    @Test
    @DisplayName("findByIdWithLock: Applies pessimistic write lock and projects ProviderAndServiceProjection")
    void findByIdWithLock_ReturnsProjectionWithLock() {
        // Act
        Optional<ProviderAndServiceProjection> projection = providerRepository.findByIdWithLock(provider1.getId(), service1.getId());

        // Assert
        assertThat(projection).isPresent();
        assertThat(projection.get().provider().getId()).isEqualTo(provider1.getId());
        assertThat(projection.get().service().getId()).isEqualTo(service1.getId());
    }
}