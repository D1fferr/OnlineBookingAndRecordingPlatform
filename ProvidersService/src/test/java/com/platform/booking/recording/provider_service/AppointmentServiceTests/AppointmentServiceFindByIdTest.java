package com.platform.booking.recording.provider_service.AppointmentServiceTests;

import com.platform.booking.recording.provider_service.dtos.AppointmentGetDTO;
import com.platform.booking.recording.provider_service.exceptions.AppointmentNotFoundException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceFindByIdTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @DisplayName("findById: Successfully returns mapped DTO when appointment exists")
    void findById_Success() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        AppointmentGetDTO expectedDTO = new AppointmentGetDTO();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));
        when(appointmentMapper.entityToGetDTO(appointment))
                .thenReturn(expectedDTO);

        // Act
        AppointmentGetDTO result = appointmentService.findById(appointmentId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentMapper, times(1)).entityToGetDTO(appointment);
    }

    @Test
    @DisplayName("findById: Throws AppointmentNotFoundException when appointment does not exist")
    void findById_NotFound_ThrowsException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.findById(appointmentId));

        verify(appointmentMapper, never()).entityToGetDTO(any());
    }
}
