package com.platform.booking.recording.provider_service.AppointmentServiceTests;

import com.platform.booking.recording.provider_service.dtos.AppointmentGetDTO;
import com.platform.booking.recording.provider_service.dtos.AppointmentPageDTO;
import com.platform.booking.recording.provider_service.models.Appointment;
import com.platform.booking.recording.provider_service.repositories.AppointmentRepository;
import com.platform.booking.recording.provider_service.services.AppointmentService;
import com.platform.booking.recording.provider_service.util.AppointmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceFindAppointmentsByProviderTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("findAppointmentsByProvider: Successfully fetches mapped page of appointments")
    void findAppointmentsByProvider_Success() {
        // Arrange
        String rawSearch = "john doe";
        String expectedSearchPattern = "%john%doe%";
        UUID providerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());

        AppointmentGetDTO getDTO = new AppointmentGetDTO();

        Page<Appointment> appointmentPage = new PageImpl<>(List.of(appointment), pageable, 1);

        when(appointmentRepository.findAppointments(eq(expectedSearchPattern), eq(providerId), eq(pageable)))
                .thenReturn(appointmentPage);
        when(appointmentMapper.entityToGetDTO(appointment)).thenReturn(getDTO);

        // Act
        AppointmentPageDTO result = appointmentService.findAppointmentsByProvider(rawSearch, providerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getDtoList().size());
        assertEquals(getDTO, result.getDtoList().get(0));

        verify(appointmentRepository, times(1))
                .findAppointments(expectedSearchPattern, providerId, pageable);
        verify(appointmentMapper, times(1)).entityToGetDTO(appointment);
    }

    @Test
    @DisplayName("findAppointmentsByProvider: Returns empty page DTO when no appointments match")
    void findAppointmentsByProvider_EmptyPage() {
        // Arrange
        String rawSearch = "nonexistent";
        String expectedSearchPattern = "%nonexistent%";
        UUID providerId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Appointment> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(appointmentRepository.findAppointments(eq(expectedSearchPattern), eq(providerId), eq(pageable)))
                .thenReturn(emptyPage);

        // Act
        AppointmentPageDTO result = appointmentService.findAppointmentsByProvider(rawSearch, providerId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getDtoList().isEmpty());

        verify(appointmentMapper, never()).entityToGetDTO(any());
    }
}
