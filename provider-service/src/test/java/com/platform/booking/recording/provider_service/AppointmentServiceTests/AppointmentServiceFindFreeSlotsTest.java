package com.platform.booking.recording.provider_service.AppointmentServiceTests;

import com.platform.booking.recording.provider_service.dtos.AvailableSlotsResponseDTO;
import com.platform.booking.recording.provider_service.dtos.DaySlotsDTO;
import com.platform.booking.recording.provider_service.dtos.FreeSlotDTO;
import com.platform.booking.recording.provider_service.exceptions.ServiceProviderNotFoundException;
import com.platform.booking.recording.provider_service.models.Appointment;
import com.platform.booking.recording.provider_service.models.Provider;
import com.platform.booking.recording.provider_service.models.ServiceProvider;
import com.platform.booking.recording.provider_service.models.WorkingHours;
import com.platform.booking.recording.provider_service.repositories.ProviderRepository;
import com.platform.booking.recording.provider_service.repositories.ServiceRepository;
import com.platform.booking.recording.provider_service.services.AppointmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceFindFreeSlotsTest {
    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("findFreeSlots: Successfully generates slots ignoring lunch breaks and existing bookings")
    void findFreeSlots_Success() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setTimezone("UTC");

        // Working hours for current day
        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setDayOfWeek(currentDayOfWeek);
        workingHours.setIsActive(true);
        workingHours.setStartTime(LocalTime.of(9, 0));
        workingHours.setEndTime(LocalTime.of(17, 0));
        workingHours.setBreakStartTime(LocalTime.of(13, 0));
        workingHours.setBreakEndTime(LocalTime.of(14, 0));
        workingHours.setSlotStep(30);

        provider.setWorkingHours(Set.of(workingHours));

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(serviceId);
        serviceProvider.setDuration(30);
        serviceProvider.setProvider(provider);

        // Booked appointment overlapping with 10:00 - 10:30
        Appointment appointment = new Appointment();
        appointment.setStartTime(OffsetDateTime.now().with(LocalTime.of(10, 0)));
        appointment.setEndTime(OffsetDateTime.now().with(LocalTime.of(10, 30)));

        when(serviceRepository.findWithProviderAndWorkingHoursById(serviceId))
                .thenReturn(Optional.of(serviceProvider));
        when(providerRepository.findBookedAppointmentsForPeriod(eq(providerId), any(), any()))
                .thenReturn(List.of(appointment));

        // Act
        AvailableSlotsResponseDTO result = appointmentService.findFreeSlots(serviceId);

        // Assert
        assertNotNull(result);
        assertEquals("UTC", result.getTimezone());
        assertFalse(result.getAppointments().isEmpty());

        verify(serviceRepository, times(1)).findWithProviderAndWorkingHoursById(serviceId);
        verify(providerRepository, times(1)).findBookedAppointmentsForPeriod(eq(providerId), any(), any());
    }

    @Test
    @DisplayName("findFreeSlots: Throws ServiceProviderNotFoundException when service does not exist")
    void findFreeSlots_ServiceNotFound_ThrowsException() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.findWithProviderAndWorkingHoursById(serviceId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ServiceProviderNotFoundException.class,
                () -> appointmentService.findFreeSlots(serviceId));

        verify(providerRepository, never()).findBookedAppointmentsForPeriod(any(), any(), any());
    }
    @Test
    @DisplayName("findFreeSlots: Filters out slots overlapping with lunch break")
    void findFreeSlots_ExcludesLunchBreakSlots() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setTimezone("Europe/Kyiv");

        // Working hours: 12:00 to 15:00, Lunch: 13:00 to 14:00, Duration: 30 min
        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setDayOfWeek(currentDayOfWeek);
        workingHours.setIsActive(true);
        workingHours.setStartTime(LocalTime.of(12, 0));
        workingHours.setEndTime(LocalTime.of(15, 0));
        workingHours.setBreakStartTime(LocalTime.of(13, 0));
        workingHours.setBreakEndTime(LocalTime.of(14, 0));
        workingHours.setSlotStep(30);

        provider.setWorkingHours(Set.of(workingHours));

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(serviceId);
        serviceProvider.setDuration(30);
        serviceProvider.setProvider(provider);

        when(serviceRepository.findWithProviderAndWorkingHoursById(serviceId))
                .thenReturn(Optional.of(serviceProvider));
        when(providerRepository.findBookedAppointmentsForPeriod(eq(providerId), any(), any()))
                .thenReturn(List.of());

        // Act
        AvailableSlotsResponseDTO result = appointmentService.findFreeSlots(serviceId);

        // Assert
        DaySlotsDTO todaySlots = result.getAppointments().stream()
                .filter(d -> d.getDayOfWeek() == currentDayOfWeek)
                .findFirst()
                .orElseThrow();

        List<FreeSlotDTO> freeSlots = todaySlots.getFreeSlots();

        // Slots starting at 13:00 and 13:30 must NOT be present
        boolean containsLunchSlot1 = freeSlots.stream()
                .anyMatch(slot -> slot.getStartTime().equals(LocalTime.of(13, 0)));
        boolean containsLunchSlot2 = freeSlots.stream()
                .anyMatch(slot -> slot.getStartTime().equals(LocalTime.of(13, 30)));

        assertFalse(containsLunchSlot1, "Slot starting at 13:00 should be excluded due to lunch break");
        assertFalse(containsLunchSlot2, "Slot starting at 13:30 should be excluded due to lunch break");
    }

    @Test
    @DisplayName("findFreeSlots: Filters out slots overlapping with existing booked appointments")
    void findFreeSlots_ExcludesBookedSlots() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();

        Provider provider = new Provider();
        provider.setId(providerId);
        provider.setTimezone("Europe/Kyiv");

        int currentDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        WorkingHours workingHours = new WorkingHours();
        workingHours.setDayOfWeek(currentDayOfWeek);
        workingHours.setIsActive(true);
        workingHours.setStartTime(LocalTime.of(0, 0));
        workingHours.setEndTime(LocalTime.of(23, 59));
        workingHours.setSlotStep(60);

        provider.setWorkingHours(Set.of(workingHours));

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(serviceId);
        serviceProvider.setDuration(60);
        serviceProvider.setProvider(provider);

        // Booked appointment from 15:00 to 16:00 today
        Appointment bookedAppointment = new Appointment();
        OffsetDateTime todayAt15 = OffsetDateTime.now(ZoneOffset.UTC)
                .with(LocalTime.of(15, 0));
        bookedAppointment.setStartTime(todayAt15);
        bookedAppointment.setEndTime(todayAt15.plusHours(1));

        when(serviceRepository.findWithProviderAndWorkingHoursById(serviceId))
                .thenReturn(Optional.of(serviceProvider));
        when(providerRepository.findBookedAppointmentsForPeriod(eq(providerId), any(), any()))
                .thenReturn(List.of(bookedAppointment));

        // Act
        AvailableSlotsResponseDTO result = appointmentService.findFreeSlots(serviceId);

        // Assert
        DaySlotsDTO todaySlots = result.getAppointments().stream()
                .filter(d -> d.getDayOfWeek() == currentDayOfWeek)
                .findFirst()
                .orElseThrow();

        boolean containsBookedSlot = todaySlots.getFreeSlots().stream()
                .anyMatch(slot -> slot.getStartTime().equals(LocalTime.of(15, 0)));

        assertFalse(containsBookedSlot, "Slot at 15:00 must be excluded because it is already booked");
    }
}
