package com.platform.booking.recording.ProvidersService.repositories;

import com.platform.booking.recording.ProvidersService.models.Appointment;
import com.platform.booking.recording.ProvidersService.models.AppointmentsStatus;
import com.platform.booking.recording.ProvidersService.models.Provider;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRepositoryTest extends AbstractBaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Provider provider;
    private Appointment appointment1;
    private Appointment appointment2;
    private UUID token1;

    @BeforeEach
    void setUp() {
        // Create and persist Provider
        provider = new Provider();
        provider.setId(UUID.randomUUID());
        provider.setName("Dr. Alex House");
        provider.setEmail("alex.house@example.com");
        provider.setServiceType("MEDICAL");
        provider.setTimezone("UTC");
        provider.setAvatarURL("https://example.com/avatar.png");
        provider.setCreatedAt(OffsetDateTime.now());
        provider.setIsBlocked(false);
        entityManager.persist(provider);

        // Appointment 1: 10:00 - 11:00
        token1 = UUID.randomUUID();
        OffsetDateTime baseTime = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0);
        appointment1 = createAppointment(provider, "John Doe", "john@example.com", "Regular checkup", baseTime, baseTime.plusHours(1), token1);
        entityManager.persist(appointment1);

        // Appointment 2: 14:00 - 15:00
        appointment2 = createAppointment(provider, "Alice Smith", "alice@example.com", "Follow-up consultation", baseTime.plusHours(4), baseTime.plusHours(5), UUID.randomUUID());
        entityManager.persist(appointment2);

        entityManager.flush();
        entityManager.clear();
    }

    private Appointment createAppointment(Provider provider, String clientName, String clientEmail, String comment, OffsetDateTime startTime, OffsetDateTime endTime, UUID token) {
        Appointment appointment = new Appointment();
        // ID is auto-generated via @UuidGenerator
        appointment.setProvider(provider);
        appointment.setClientName(clientName);
        appointment.setClientEmail(clientEmail);
        appointment.setClientComment(comment);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentsStatus.CONFIRMED);
        appointment.setIsReminderSent(false);
        appointment.setSecureToken(token);
        appointment.setCreatedAt(OffsetDateTime.now());
        return appointment;
    }

    @Test
    @DisplayName("existsOverlappingAppointment: Detects overlapping time intervals correctly")
    void existsOverlappingAppointment_DetectsOverlap() {
        OffsetDateTime baseTime = appointment1.getStartTime();

        // 1. Partial overlap at start (09:30 - 10:30)
        boolean overlapStart = appointmentRepository.existsOverlappingAppointment(
                provider.getId(), baseTime.minusMinutes(30), baseTime.plusMinutes(30)
        );

        // 2. Exact match (10:00 - 11:00)
        boolean exactOverlap = appointmentRepository.existsOverlappingAppointment(
                provider.getId(), baseTime, appointment1.getEndTime()
        );

        // 3. No overlap - adjacent interval (11:00 - 12:00)
        boolean noOverlapAdjacent = appointmentRepository.existsOverlappingAppointment(
                provider.getId(), appointment1.getEndTime(), appointment1.getEndTime().plusHours(1)
        );

        assertThat(overlapStart).isTrue();
        assertThat(exactOverlap).isTrue();
        assertThat(noOverlapAdjacent).isFalse();
    }

    @Test
    @DisplayName("findByIdWithProvider: Eagerly fetches provider relationship")
    void findByIdWithProvider_EagerlyLoadsProvider() {
        // Act
        Optional<Appointment> result = appointmentRepository.findByIdWithProvider(appointment1.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(Hibernate.isInitialized(result.get().getProvider())).isTrue();
        assertThat(result.get().getProvider().getName()).isEqualTo("Dr. Alex House");
    }

    @Test
    @DisplayName("findBySecureTokenWithProvider: Retrieves appointment and provider by secure token")
    void findBySecureTokenWithProvider_ReturnsAppointment() {
        // Act
        Optional<Appointment> result = appointmentRepository.findBySecureTokenWithProvider(token1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(appointment1.getId());
        assertThat(Hibernate.isInitialized(result.get().getProvider())).isTrue();
    }

    @Test
    @DisplayName("findAllByProvider_Id: Returns paginated appointments for provider")
    void findAllByProvider_Id_ReturnsPaginatedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Appointment> page = appointmentRepository.findAllByProvider_Id(provider.getId(), pageable);

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("findAppointments: Filters by client name, email, or comment matching search pattern")
    void findAppointments_FiltersBySearchTerm() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert 1: Search by partial email
        Page<Appointment> emailSearch = appointmentRepository.findAppointments("%john%", provider.getId(), pageable);
        assertThat(emailSearch.getContent())
                .extracting(Appointment::getId)
                .containsExactly(appointment1.getId());

        // Act & Assert 2: Search by partial comment
        Page<Appointment> commentSearch = appointmentRepository.findAppointments("%consultation%", provider.getId(), pageable);
        assertThat(commentSearch.getContent())
                .extracting(Appointment::getId)
                .containsExactly(appointment2.getId());
    }
}
